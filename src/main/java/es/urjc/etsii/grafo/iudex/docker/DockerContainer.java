package es.urjc.etsii.grafo.iudex.docker;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import es.urjc.etsii.grafo.iudex.entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.InternalServerErrorException;
import es.urjc.etsii.grafo.iudex.exceptions.UnacceptedLanguageException;
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
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DockerContainer {
    private static final Logger logger = LoggerFactory.getLogger(DockerContainer.class);
    private static DockerClient dockerClient = null;

    private final Set<String> acceptedLanguages = Set.of("c", "cpp", "java", "sql", "python3");

    public DockerContainer(DockerClient dockerClient) {
        DockerContainer.dockerClient = dockerClient;
    }

    public Result ejecutar(Result result, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String imagenId) throws IOException {
        String language = result.getLanguage().getNombreLenguaje();
        if (!acceptedLanguages.contains(language)) { throw new UnacceptedLanguageException("Language is not accepted"); }

        logger.debug("Building {} container for image {}", language, imagenId);

        String nombreClase = (language.equals("java")) ? this.getJavaClassName(result) : result.getFileName();
        String nombreDocker = "a" + result.getId() + "_" + java.time.LocalDateTime.now();
        nombreDocker = nombreDocker.replace(":", "");

        //Creamos el contendor
        HostConfig hostConfig = new HostConfig().withMemory(Long.parseLong(defaultMemoryLimit)).withCpusetCpus(defaultCPU);
        String[] env = this.getEnv(result, language, defaultTimeout);
        try (CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imagenId)
                .withNetworkDisabled(true)
                .withEnv(env)
                .withHostConfig(hostConfig)
                .withName(nombreDocker)) {
            CreateContainerResponse container = createContainerCmd.exec();
            logger.debug("DOCKER {}: Running container for result {} with timeout {} and memory limit {}", language, result.getId(), result.getMaxTimeout(), result.getMaxMemory());

            copyInputDataToContainer(container, result, nombreClase + "." + this.getFileExtension(language));

            dockerClient.startContainerCmd(container.getId()).exec();

            waitUntilContainerStopped(container);
            setReturnedValuesFromContainer(container, result, language);

            dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();
        }

        logger.debug("DOCKER {}: Finish running container for result {} ", language, result.getId());
        return result;
    }

    private void copyInputDataToContainer(CreateContainerResponse container, Result result, String codeFileName) throws IOException {
        //Copiamos el codigo
        copiarArchivoAContenedor(container.getId(), codeFileName, result.getCodigo(), "/root");

        //Copiamos la entrada
        copiarArchivoAContenedor(container.getId(), "entrada.in", result.getEntrada(), "/root");
    }

    private void setReturnedValuesFromContainer(CreateContainerResponse container, Result result, String language) throws IOException {
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

        this.setSignals(result, container, language);
    }

    private void waitUntilContainerStopped(CreateContainerResponse container) {
        Boolean isRunning;
        do {
            isRunning = dockerClient.inspectContainerCmd(container.getId()).exec().getState().getRunning();
        } while (isRunning != null && isRunning);  //Mientras esta corriendo se hace el do
    }

    private String getFileExtension(String language) {
        switch (language) {
            case "sql" -> { return "sql"; }
            case "java" -> { return "java"; }
            case "c" -> { return "c"; }
            case "cpp" -> { return "cpp"; }
            case "python3" -> { return "py"; }
            default -> throw new UnacceptedLanguageException("Language is not accepted");
        }
    }

    private String[] getEnv(Result result, String language, String defaultTimeout) {
        String timeout;
        if (result.getMaxTimeout() != null) { timeout = result.getMaxTimeout(); }
        else { timeout = defaultTimeout; }

        List<String> env2 = new ArrayList<>();
        env2.add("EXECUTION_TIMEOUT=" + timeout);
        env2.add("FILENAME2=" + getFileName2(result, getFileExtension(language)));

        if (!language.equals("py")) { env2.add("FILENAME1=" + getFileName1(result, language)); }

        if (language.equals("java")) { env2.add("MEMORYLIMIT=" + "-Xmx" + result.getMaxMemory() + "m"); }

        return env2.toArray(new String[0]);
    }

    private String getFileName1(Result result, String language) {
        switch (language) {
            case "sql" -> { return "entrada.in"; }
            case "python3", "cpp", "c" -> { return result.getFileName(); }
            case "java" -> { return getJavaClassName(result); }
            default -> throw new UnacceptedLanguageException("Language is not accepted");
        }
    }

    private String getFileName2(Result result, String fileExtension) {
        if (fileExtension.equals("java")) { return this.getJavaClassName(result); }
        else { return result.getFileName() + "." + fileExtension; }
    }

    private String getJavaClassName(Result result) {
        Matcher publicClassMatcher = Pattern.compile("public\\s+class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)")
                                       .matcher(result.getCodigo());
        Matcher privateClassMatcher = Pattern.compile("class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)")
                                        .matcher(result.getCodigo());

        if (publicClassMatcher.find()) { return publicClassMatcher.group(1); }
        else if (privateClassMatcher.find()) { return privateClassMatcher.group(1); }

        return "";
    }

    private void setSignals(Result result, CreateContainerResponse container, String language) throws IOException {
        if (language.equals("python3")) {
            String signal = copiarArchivoDeContenedor(container.getId(), "root/signal.txt");
            result.setSignalEjecutor(signal);
        } else {
            String signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
            result.setSignalEjecutor(signalEjecutor);

            String signalCompilador = copiarArchivoDeContenedor(container.getId(), "root/signalCompilador.txt");
            result.setSignalCompilador(signalCompilador);
        }
    }

    static void copiarArchivoAContenedor(String contAux, String nombre, String contenido, String pathDestino) throws IOException {
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
    static void waitUntil(BooleanSupplier condition, long timeoutms) throws TimeoutException {
        long start = System.currentTimeMillis();
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() - start > timeoutms) {
                throw new TimeoutException(String.format("Condition not meet within %s ms", timeoutms));
            }
        }
    }

    //sacado de aqui https://github.com/docker-java/docker-java/issues/991
    String copiarArchivoDeContenedor(String contAux, String pathOrigen) throws IOException {
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
