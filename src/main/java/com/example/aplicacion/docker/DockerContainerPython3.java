package com.example.aplicacion.docker;

import com.example.aplicacion.entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

//Clase que se encarga de lanzar los docker de tipo Python
public class DockerContainerPython3 extends DockerContainer {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainerPython3.class);

    public DockerContainerPython3(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit) {
        super(result, dockerClient, defaultMemoryLimit, defaultTimeout, defaultCPU, defaultStorageLimit);
    }

    public Result ejecutar(String imagenId) throws IOException {
        logger.debug("Building container for image {} ", imagenId);
        String defaultCPU = (this.getDefaultCPU());
        Long defaultMemoryLimit = Long.parseLong(this.getDefaultMemoryLimit());

        Result result = getResult();
        String nombreClase = result.getFileName();
        String nombreDocker = "a" + result.getId() + "_" + java.time.LocalDateTime.now();
        nombreDocker = nombreDocker.replace(":", "");

        DockerClient dockerClient = getDockerClient();
        //Creamos el contendor
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(defaultMemoryLimit).withCpusetCpus(defaultCPU);

        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).withEnv("EXECUTION_TIMEOUT=" + result.getMaxTimeout(), "FILENAME2=" + nombreClase + ".py").withHostConfig(hostConfig).withName(nombreDocker).exec();

        logger.debug("DOCKER PYTHON3: Running container for result {} with timeout {} and memory limit {}" + result.getId(), result.getMaxTimeout(), result.getMaxMemory());

        //Copiamos el codigo
        copiarArchivoAContenedor(container.getId(), nombreClase + ".py", result.getCodigo(), "/root");

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

        String signal = copiarArchivoDeContenedor(container.getId(), "root/signal.txt");
        result.setSignalEjecutor(signal);

        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        logger.debug("DOCKER PYTHON3: Finish running container for result " + result.getId() + " ");
        return result;
    }
}
