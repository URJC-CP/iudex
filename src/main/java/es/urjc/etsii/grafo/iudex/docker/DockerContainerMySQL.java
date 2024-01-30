package es.urjc.etsii.grafo.iudex.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import es.urjc.etsii.grafo.iudex.entities.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DockerContainerMySQL extends DockerContainer {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainerMySQL.class);

    public DockerContainerMySQL(DockerClient dockerClient) {
        super(dockerClient);
    }

    @Override
    public Result ejecutar(Result result, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String imagenId) throws IOException, InterruptedException {
        logger.debug("Building container for image {}", imagenId);

        String nombreClase = this.getClassName(result);
        String nombreDocker = "iudex_" + result.getId() + "_" + java.time.LocalDateTime.now();
        nombreDocker = nombreDocker.replace(":", "");

        String timeout = getTimeoutEnv(result, defaultTimeout);

        //Creamos el contendor
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(Long.parseLong(defaultMemoryLimit)).withCpusetCpus(defaultCPU);

        try (CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imagenId)
                .withNetworkDisabled(true)
                .withEnv(this.getEnv(result, defaultTimeout))
                .withHostConfig(hostConfig).withName(nombreDocker)) {
            CreateContainerResponse container = createContainerCmd.exec();
            logger.debug("DOCKER MySQL: Running container for result {} with timeout {} and memory limit {}", result.getId(), timeout, result.getMaxMemory());

            copyInputDataToContainer(container, result, nombreClase + ".sql");

            //Arrancamos el docker
            dockerClient.startContainerCmd(container.getId()).exec();
            // TODO chapucero, hardcodeado esperar 10 segundos a que este la bbdd lista, deberia ser cuando este el puerto 3306 ok
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logger.error("context", e);
                dockerClient.stopContainerCmd(container.getId()).exec();
                dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();
                throw e;
            }

            var executionID = dockerClient.execCreateCmd(container.getId()).withAttachStdout(true).withCmd("bash", "-c", "mysql -h localhost -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <$FILENAME1 >salidaError.ans 2>&1 && mysql -h localhost -u$MYSQL_USER -p$MYSQL_PASSWORD $MYSQL_DATABASE <$FILENAME2 >salidaEstandar.ans 2>salidaError.ans; echo $? >>signalEjecutor.txt").exec().getId();
            // Starting the execution
            try {
                dockerClient.execStartCmd(executionID).exec(new ExecStartResultCallback(System.out, System.err)).awaitCompletion();
            } catch (InterruptedException e) {
                logger.error("context", e);
                dockerClient.stopContainerCmd(container.getId()).exec();
                dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();
                throw e;
            }

            LocalDateTime maxTime = LocalDateTime.now().plusSeconds(Long.parseLong(result.getMaxTimeout()));
            String signalEjecutor;
            do {
                Thread.sleep(200);
                signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
            } while (LocalDateTime.now().isBefore(maxTime) && signalEjecutor.isEmpty());

            dockerClient.stopContainerCmd(container.getId()).exec();
            //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
            this.waitUntilContainerStopped(container);
            this.setReturnedValuesFromContainer(container, result, signalEjecutor);

            dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();
        }

        logger.debug("DOCKER MySQL: Finish running container for result {} ", result.getId());
        return result;
    }

    @Override
    protected String getClassName(Result result) {
        return result.getFileName();
    }

    @Override
    protected String getFileExtension() {
        return "sql";
    }

    @Override
    protected String getFileName1(Result result) {
        return "entrada.in";
    }

    @Override
    protected String getFileName2(Result result, String fileExtension) {
        return result.getFileName() + "." + fileExtension;
    }

    @Override
    protected String[] getEnv(Result result, String defaultTimeout) {
        List<String> env = new ArrayList<>();

        env.add("EXECUTION_TIMEOUT=" + getTimeoutEnv(result, defaultTimeout));
        env.add("FILENAME2=" + this.getClassName(result) + ".sql");
        env.add("FILENAME1=" + "entrada.in");

        return env.toArray(new String[0]);
    }

    protected void setReturnedValuesFromContainer(CreateContainerResponse container, Result result, String signalEjecutor) throws IOException {
        //Buscamos la salida Estandar
        String salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");
        result.setSalidaEstandar(salidaEstandar);

        //buscamos la salida Error
        String salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");
        result.setSalidaError(salidaError);

        String time = copiarArchivoDeContenedor(container.getId(), "root/time.txt");
        result.setSalidaTime(time);
    }

    @Override
    protected void setSignals(Result result, CreateContainerResponse container) throws IOException {
        //para que no de fallo de compilador
        result.setSignalCompilador("0");

        String signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        result.setSignalEjecutor(signalEjecutor);
    }
}
