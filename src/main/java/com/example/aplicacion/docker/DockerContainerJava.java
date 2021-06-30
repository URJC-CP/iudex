package com.example.aplicacion.docker;

import com.example.aplicacion.entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Clase que se encarga de lanzar los docker de tipo JAVA
public class DockerContainerJava extends DockerContainer {

    Logger logger = LoggerFactory.getLogger(DockerContainerJava.class);

    public DockerContainerJava(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit) {
        super(result, dockerClient, defaultMemoryLimit, defaultTimeout, defaultCPU, defaultStorageLimit);
    }

    public Result ejecutar(String imagenId) throws IOException {
        logger.debug("Building container for image {}", imagenId);
        String defaultCPU = (this.getDefaultCPU());
        Long defaultMemoryLimit = Long.parseLong(this.getDefaultMemoryLimit());

        String nombreClase = getClassName();
        Result result = getResult();
        String nombreDocker = "a" + result.getId() + "_" + java.time.LocalDateTime.now();
        nombreDocker = nombreDocker.replace(":", "");
        String timeout;
        if (result.getMaxTimeout() != null) {
            timeout = result.getMaxTimeout();
        } else {
            timeout = this.getDefaultTimeout();
        }

        DockerClient dockerClient = getDockerClient();
        //Creamos el contendor
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(defaultMemoryLimit).withCpusetCpus(defaultCPU);
        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).withEnv("EXECUTION_TIMEOUT=" + timeout, "FILENAME1=" + nombreClase, "FILENAME2=" + getClassName(), "MEMORYLIMIT=" + "-Xmx" + result.getMaxMemory() + "m").withHostConfig(hostConfig).withName(nombreDocker).exec();

        logger.debug("DOCKER JAVA: Running container for result {} with timeout {} and memory limit {}", result.getId(), timeout, result.getMaxMemory());

        //Copiamos el codigo
        copiarArchivoAContenedor(container.getId(), nombreClase + ".java", result.getCodigo(), "/root");

        //Copiamos la entrada
        copiarArchivoAContenedor(container.getId(), "entrada.in", result.getEntrada(), "/root");

        //Arrancamos el docker
        dockerClient.startContainerCmd(container.getId()).exec();
        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        Boolean isRunning = null;
        do {
            isRunning = dockerClient.inspectContainerCmd(container.getId()).exec().getState().getRunning();
        } while (isRunning != null && isRunning.booleanValue());  //Mientras esta corriendo se hace el do

        //Buscamos la salida Estandar
        String salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");
        result.setSalidaEstandar(salidaEstandar);

        //buscamos la salida Error
        String salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");
        result.setSalidaError(salidaError);

        //buscamos la salida Compilador
        String salidaCompilador = copiarArchivoDeContenedor(container.getId(), "root/salidaCompilador.ans");
        result.setSalidaCompilador(salidaCompilador);

        String time = copiarArchivoDeContenedor(container.getId(), "root/time.txt");
        result.setSalidaTime(time);

        String signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        result.setSignalEjecutor(signalEjecutor);

        String signalCompilador = copiarArchivoDeContenedor(container.getId(), "root/signalCompilador.txt");
        result.setSignalCompilador(signalCompilador);

        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        logger.debug("DOCKER JAVA: Finish running container for result {} ", result.getId());
        return result;
    }

    private String getClassName() {
        String salida = "";
        //Primero buscamos si existe una classe tipo "public class.."
        Pattern p = Pattern.compile("public\\s+class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)");
        Matcher m = p.matcher(getResult().getCodigo());
        if (m.find()) {
            salida = m.group(1);
        }
        //Si no, buscamos la clase q no es publica
        else {
            Pattern p2 = Pattern.compile("class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)");
            Matcher m2 = p2.matcher(getResult().getCodigo());
            if (m2.find()) {
                salida = m2.group(1);
            }
        }
        return salida;
    }
}
