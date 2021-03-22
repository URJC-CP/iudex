package com.example.aplicacion.Docker;

import com.example.aplicacion.Entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DockerContainerMySQL extends DockerContainer {

    Logger logger = LoggerFactory.getLogger(DockerContainerMySQL.class);

    public DockerContainerMySQL(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit) {
        super(result, dockerClient, defaultMemoryLimit, defaultTimeout, defaultCPU, defaultStorageLimit);
    }

    public Result ejecutar(String imagenId) throws IOException {
        logger.debug("Building container for image " + imagenId);
        String defaultCPU = (this.getDefaultCPU());
        Long defaultMemoryLimit = Long.parseLong(this.getDefaultMemoryLimit());
        String defaultStorageLimit = this.getDefaultStorageLimit();

        Result result = getResult();
        String nombreClase = result.getFileName();
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
        //hostConfig.withMemory(defaultMemoryLimit).withMemorySwap(defaultMemoryLimit).withStorageOpt(Map.ofEntries(Map.entry("size", defaultStorageLimit))).withCpusetCpus(defaultCPU);
        hostConfig.withMemory(defaultMemoryLimit).withCpusetCpus(defaultCPU);

        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).withEnv("EXECUTION_TIMEOUT=" + result.getMaxTimeout(), "FILENAME1=entrada.in", "FILENAME2=" + nombreClase + ".sql").withHostConfig(hostConfig).withName(nombreDocker).exec();
        logger.debug("DOCKER MySQL: Container built for result" + result.getId() + " with timeout " + result.getMaxTimeout() + " and memory limit " + result.getMaxMemory());

        //Copiamos el codigo
        copiarArchivoAContenedor(container.getId(), nombreClase + ".sql", result.getCodigo(), "/root");

        //Copiamos la entrada
        copiarArchivoAContenedor(container.getId(), "entrada.in", result.getEntrada(), "/root");

        //Arrancamos el docker
        dockerClient.startContainerCmd(container.getId()).exec();
        //ejecutar instrucciones de la entrega
        //dockerClient.execStartCmd(container.getId()).withTty(true).exec("mysql -h localhost -u root test <entrada.in && mysql -h localhost -u root test <$FILENAME1 > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt");

        var executionID = dockerClient
            .execCreateCmd(container.getId())
            .withCmd("/bin/bash -c 'mysql -h localhost -u root -p$MYSQL_ROOT_PASSWORD test <entrada.in && mysql -h localhost -u root -p$MYSQL_ROOT_PASSWORD test <$FILENAME2 > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt'")
            .exec()
            .getId();
        // Starting the execution
        try {
            dockerClient
                .execStartCmd(executionID)
                .withTty(true)
                .exec(new ExecStartResultCallback(System.out, System.err))
                .awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        InspectContainerResponse inspectContainerResponse = null;
        do {
            inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        } while (inspectContainerResponse.getState().getRunning());  //Mientras esta corriendo se hace el do

        //Buscamos la salida Estandar
        String salidaEstandar = null;
        salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");
        result.setSalidaEstandar(salidaEstandar);

        //buscamos la salida Error
        String salidaError = null;
        salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");
        result.setSalidaError(salidaError);

        String time = null;
        time = copiarArchivoDeContenedor(container.getId(), "root/time.txt");
        result.setSalidaTime(time);

        //para que no de fallo de compilador
        result.setSignalCompilador("0");

        String signalEjecutor = null;
        signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        result.setSignalEjecutor(signalEjecutor);

        //logger.info("DOCKER MySQL: EL result "+result.getId() + " ha terminado con senyal "+ signal);

        //TODO:descomentar
        //dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        logger.debug("DOCKER MySQL: Finished running container for result " + result.getId() + " ");
        return result;
    }
}
