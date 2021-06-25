package com.example.aplicacion.docker;

import com.example.aplicacion.entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

public class DockerContainerMySQL extends DockerContainer {

    Logger logger = LoggerFactory.getLogger(DockerContainerMySQL.class);

    public DockerContainerMySQL(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit) {
        super(result, dockerClient, defaultMemoryLimit, defaultTimeout, defaultCPU, defaultStorageLimit);
    }

    public Result ejecutar(String imagenId) throws IOException {
        logger.debug("Building container for image {}", imagenId);
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
        logger.debug("DOCKER MySQL: Running container for result {} with timeout {} and memory limit {}", result.getId(), result.getMaxTimeout(), result.getMaxMemory());

        //Copiamos el codigo
        copiarArchivoAContenedor(container.getId(), nombreClase + ".sql", result.getCodigo(), "/root");

        //Copiamos la entrada
        copiarArchivoAContenedor(container.getId(), "entrada.in", result.getEntrada(), "/root");

        //Arrancamos el docker
        dockerClient.startContainerCmd(container.getId()).exec();
        //ejecutar instrucciones de la entrega
        //dockerClient.execStartCmd(container.getId()).withTty(true).exec("mysql -h localhost -u root test <entrada.in && mysql -h localhost -u root test <$FILENAME1 > salidaEstandar.ans 2> salidaError.ans; echo $? >> signalEjecutor.txt");

        // TODO chapucero, hardcodeado esperar 10 segundos a que este la bbdd lista, deberia ser cuando este el puerto 3306 ok
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        var executionID = dockerClient.execCreateCmd(container.getId()).withAttachStdout(true).withCmd("bash", "-c", "mysql -h localhost -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <$FILENAME1 >salidaError.ans 2>&1 && mysql -h localhost -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <$FILENAME2 >salidaEstandar.ans 2>salidaError.ans; echo $? >>signalEjecutor.txt").exec().getId();
        // Starting the execution
        try {
            dockerClient.execStartCmd(executionID).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LocalDateTime maxTime = LocalDateTime.now().plusSeconds(Long.parseLong(result.getMaxTimeout()));
        String signalEjecutor = null;
        do {
            Thread.onSpinWait();
            signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        } while (LocalDateTime.now().isBefore(maxTime) && signalEjecutor.equals(""));

        dockerClient.stopContainerCmd(container.getId()).exec();

        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        InspectContainerResponse inspectContainerResponse = null;
        do {
            inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        } while (inspectContainerResponse.getState().getRunning());  //Mientras esta corriendo se hace el do

        //Buscamos la salida Estandar
        String salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");
        result.setSalidaEstandar(salidaEstandar);

        //buscamos la salida Error
        String salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");
        result.setSalidaError(salidaError);

        String time = copiarArchivoDeContenedor(container.getId(), "root/time.txt");
        result.setSalidaTime(time);

        //para que no de fallo de compilador
        result.setSignalCompilador("0");

        result.setSignalEjecutor(signalEjecutor);

        //logger.info("DOCKER MySQL: EL result "+result.getId() + " ha terminado con senyal "+ signal);
        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        logger.debug("DOCKER MySQL: Finish running container for result {} ", result.getId());
        return result;
    }
}
