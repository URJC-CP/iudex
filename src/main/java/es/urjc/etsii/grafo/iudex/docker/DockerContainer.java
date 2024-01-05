package es.urjc.etsii.grafo.iudex.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import com.github.dockerjava.api.model.HostConfig;
import es.urjc.etsii.grafo.iudex.entities.Result;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public abstract class DockerContainer {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainer.class);
    protected static DockerClient dockerClient = null;

    protected DockerContainer(DockerClient dockerClient) {
        DockerContainer.dockerClient = dockerClient;
    }

    public Result ejecutar(Result result, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String imagenId) throws IOException, InterruptedException {
        String language = result.getLanguage().getNombreLenguaje();

        logger.debug("Building {} container for image {}", language, imagenId);

        String nombreClase = this.getClassName(result);
        String nombreDocker = "iudex_" + result.getId() + "_" + java.time.LocalDateTime.now();
        nombreDocker = nombreDocker.replace(":", "");

        //Creamos el contendor
        HostConfig hostConfig = new HostConfig().withMemory(Long.parseLong(defaultMemoryLimit)).withCpusetCpus(defaultCPU);
        try (CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imagenId)
                .withNetworkDisabled(true)
                .withEnv(this.getEnv(result, defaultTimeout))
                .withHostConfig(hostConfig)
                .withName(nombreDocker)) {
            CreateContainerResponse container = createContainerCmd.exec();
            logger.debug("DOCKER {}: Running container for result {} with timeout {} and memory limit {}", language, result.getId(), result.getMaxTimeout(), result.getMaxMemory());

            copyInputDataToContainer(container, result, nombreClase + "." + this.getFileExtension());

            dockerClient.startContainerCmd(container.getId()).exec();

            waitUntilContainerStopped(container);
            setReturnedValuesFromContainer(container, result);

            dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();
        }

        logger.debug("DOCKER {}: Finish running container for result {} ", language, result.getId());
        return result;
    }

    protected abstract String getClassName(Result result);

    protected abstract String getFileExtension();

    protected abstract String getFileName1(Result result);

    protected void copyInputDataToContainer(CreateContainerResponse container, Result result, String codeFileName) throws IOException {
        //Copiamos el codigo
        copiarArchivoAContenedor(container.getId(), codeFileName, result.getCodigo(), "/root");

        //Copiamos la entrada
        copiarArchivoAContenedor(container.getId(), "entrada.in", result.getEntrada(), "/root");
    }

    protected void setReturnedValuesFromContainer(CreateContainerResponse container, Result result) throws IOException {
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

        this.setSignals(result, container);
    }

    protected void waitUntilContainerStopped(CreateContainerResponse container) throws InterruptedException {
        boolean isRunning;
        do {
            Thread.sleep(200);
            isRunning = Boolean.TRUE.equals(dockerClient.inspectContainerCmd(container.getId()).exec().getState().getRunning());
        } while (isRunning);  //Mientras esta corriendo se hace el do
    }

    protected String[] getEnv(Result result, String defaultTimeout) {
        List<String> env = new ArrayList<>();

        env.add("EXECUTION_TIMEOUT=" + getTimeoutEnv(result, defaultTimeout));
        env.add("FILENAME2=" + getFileName2(result, getFileExtension()));
        env.add("FILENAME1=" + getFileName1(result));

        return env.toArray(new String[0]);
    }

    protected String getTimeoutEnv(Result result, String defaultTimeout) {
        if (result.getMaxTimeout() != null) {
            return result.getMaxTimeout();
        } else {
            return defaultTimeout;
        }
    }

    protected abstract String getFileName2(Result result, String fileExtension);

    protected abstract void setSignals(Result result, CreateContainerResponse container) throws IOException;

    protected static void copiarArchivoAContenedor(String contAux, String nombre, String contenido, String pathDestino) throws IOException {
        dockerClient.copyArchiveToContainerCmd(contAux).withTarInputStream(convertStringtoInputStream(nombre, contenido)).withRemotePath(pathDestino).exec();
    }

    //Para que el codigo acepte bien la copia, tenemos que crear un Input Stream utilizando tar cuya primera linea corresponda al nombre del arvchivo y el resto al contenido
    //https://www.codota.com/code/java/methods/com.github.dockerjava.api.command.CopyArchiveToContainerCmd/withTarInputStream
    private static InputStream convertStringtoInputStream(String nombre, String contenido) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TarArchiveOutputStream tar = new TarArchiveOutputStream(bos);
        TarArchiveEntry entry = new TarArchiveEntry(nombre);
        entry.setSize(contenido.getBytes().length);
        entry.setMode(0700);
        tar.putArchiveEntry(entry);
        tar.write(contenido.getBytes());

        tar.closeArchiveEntry();
        tar.close();

        return new ByteArrayInputStream(bos.toByteArray());
    }

    //Sacado de aqui https://stackoverflow.com/questions/25325442/wait-x-seconds-or-until-a-condition-becomes-true/25325830#comment85873450_25325830
    protected static void waitUntil(BooleanSupplier condition, long timeoutms) throws TimeoutException {
        long start = System.currentTimeMillis();
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() - start > timeoutms) {
                throw new TimeoutException(String.format("Condition not meet within %s ms", timeoutms));
            }
        }
    }

    //sacado de aqui https://github.com/docker-java/docker-java/issues/991
    protected String copiarArchivoDeContenedor(String contAux, String pathOrigen) throws IOException {
        try {
            InputStream isSalida = dockerClient.copyArchiveFromContainerCmd(contAux, pathOrigen).exec();  //Obtenemos el InputStream del contenedor
            TarArchiveInputStream tarArchivo = new TarArchiveInputStream(isSalida);                     //Obtenemos el tar del IS
            logger.debug(String.valueOf(tarArchivo.getRecordSize()));
            return convertirTarFile(tarArchivo);                                                        //Lo traducimos
        } catch (InternalServerErrorException exception) {
            logger.error(exception.getMessage());
        }
        return "";
    }

    //Funcion que convierte un tar, lo guarda en fichero y devuelve un String
    private String convertirTarFile(TarArchiveInputStream tarIn) throws IOException {
        TarArchiveEntry tarAux = null;
        String salida = null;

        while ((tarAux = tarIn.getNextTarEntry()) != null) {
            //Buscamos el fichero a copiar
            if (!tarAux.isDirectory()) {
                //Una vez sabemos que es fichero lo copiamos
                salida = IOUtils.toString(tarIn);
            /*
                //DESCOMENTAR PARA GUARDAR EN FICHERO
                FileOutputStream fileOutput = new FileOutputStream(fichero);
                IOUtils.copy(tarIn, fileOutput);
                fileOutput.close();
            */
            }
        }

        //Comprobamos el tamano del String para evitar petar por tamano excesivo, si esta por encima enviamos un "" y notificamos
        if (salida != null && salida.length() > 100000000) {
            logger.warn("Folder size is too big : {}", salida.length());
            salida = "";
        }

        tarIn.close();
        return salida;
    }

    public static DockerClient getDockerClient() {
        return dockerClient;
    }

}
