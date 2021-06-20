package com.example.aplicacion.Docker;

import com.example.aplicacion.Entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.InternalServerErrorException;
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
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class DockerContainer {
    private static DockerClient dockerClient;
    Logger logger = LoggerFactory.getLogger(DockerContainer.class);
    private Result result;
    private String defaultMemoryLimit;
    private String defaultTimeout;
    private String defaultCPU;
    private String defaultStorageLimit;

    public DockerContainer(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit) {
        this.result = result;
        DockerContainer.dockerClient = dockerClient;
        this.defaultMemoryLimit = defaultMemoryLimit;
        this.defaultTimeout = defaultTimeout;
        this.defaultCPU = defaultCPU;
        this.defaultStorageLimit = defaultStorageLimit;
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

    public static DockerClient getDockerClient() {
        return dockerClient;
    }

    public static void setDockerClient(DockerClient dockerClient) {
        DockerContainer.dockerClient = dockerClient;
    }

    //sacado de aqui https://github.com/docker-java/docker-java/issues/991
    String copiarArchivoDeContenedor(String contAux, String pathOrigen) throws IOException {
        try {
            InputStream isSalida = dockerClient.copyArchiveFromContainerCmd(contAux, pathOrigen).exec();  //Obtenemos el InputStream del contenedor
            TarArchiveInputStream tarArchivo = new TarArchiveInputStream(isSalida);                     //Obtenemos el tar del IS
            //System.out.println(""+tarArchivo.getRecordSize());
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

                //DESCOMENTAR PARA GUARDAR EN FICHERO
                //FileOutputStream fileOutput = new FileOutputStream(fichero);
                //IOUtils.copy(tarIn, fileOutput);
                //fileOutput.close();
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getDefaultMemoryLimit() {
        return defaultMemoryLimit;
    }

    public void setDefaultMemoryLimit(String defaultMemoryLimit) {
        this.defaultMemoryLimit = defaultMemoryLimit;
    }

    public String getDefaultTimeout() {
        return defaultTimeout;
    }

    public void setDefaultTimeout(String defaultTimeout) {
        this.defaultTimeout = defaultTimeout;
    }

    public String getDefaultCPU() {
        return defaultCPU;
    }

    public void setDefaultCPU(String defaultCPU) {
        this.defaultCPU = defaultCPU;
    }

    public String getDefaultStorageLimit() {
        return defaultStorageLimit;
    }

    public void setDefaultStorageLimit(String defaultStorageLimit) {
        this.defaultStorageLimit = defaultStorageLimit;
    }
}
