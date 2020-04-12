package com.example.aplicacion.Docker;

import com.example.aplicacion.Entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;

//Clase que se encarga de lanzar los docker de tipo JAVA
public class DockerJava {

    private Result result;
    private static DockerClient dockerClient;

    public  DockerJava(Result result, DockerClient dockerClient){
        this.result = result;
        this.dockerClient = dockerClient;

    }

    public Result ejecutar(String imagenId) throws IOException {
        //Creamos el contendor
        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).exec();


        //Copiamos el codigo

        copiarArchivoAContenedor(container.getId(), "codigo.java", result.getCodigo(),  "/root");

        //Copiamos la entrada

        copiarArchivoAContenedor(container.getId(), "entrada.in", result.getEntrada(), "/root");

        //Arrancamos el docker
        dockerClient.startContainerCmd(container.getId()).exec();
        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        InspectContainerResponse inspectContainerResponse=null;
        do {
            inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        }while (inspectContainerResponse.getState().getRunning());  //Mientras esta corriendo se hace el do


        //Buscamos la salida Estandar
        String salidaEstandar=null;
        salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");

        //System.out.println(salidaEstandar);
        result.setSalidaEstandar(salidaEstandar);

        //buscamos la salida Error
        String salidaError=null;

        salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");

        //System.out.println(salidaError);
        result.setSalidaError(salidaError);

        //buscamos la salida Compilador
        String salidaCompilador=null;
        salidaCompilador = copiarArchivoDeContenedor(container.getId(), "root/salidaCompilador.ans");
        //System.out.println(salidaCompilador);
        result.setSalidaCompilador(salidaCompilador);

        String time= null;
        time = copiarArchivoDeContenedor(container.getId(), "root/time.txt");
        System.out.println(time);
        result.setSalidaTime(time);


        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        return result;
    }

    private static void copiarArchivoAContenedor (String contAux, String nombre, String contenido, String pathDestino ) throws IOException {
        dockerClient.copyArchiveToContainerCmd(contAux).withTarInputStream(convertStringtoInputStream(nombre, contenido)).withRemotePath(pathDestino).exec();
    }

    //sacado de aqui https://github.com/docker-java/docker-java/issues/991
    private static String copiarArchivoDeContenedor (String contAux, String pathOrigen) throws IOException {

        InputStream isSalida=dockerClient.copyArchiveFromContainerCmd(contAux, pathOrigen).exec();  //Obtenemos el InputStream del contenedor
        TarArchiveInputStream tarArchivo = new TarArchiveInputStream(isSalida);                     //Obtenemos el tar del IS
        return convertirTarFile(tarArchivo);                                                        //Lo traducimos

    }

    //Funcion que convierte un tar, lo guarda en fichero y devuelve un String
    private static String convertirTarFile(TarArchiveInputStream tarIn) throws IOException {
        TarArchiveEntry tarAux = null;
        String salida=null;

        while ((tarAux = tarIn.getNextTarEntry()) != null) {
            //Buscamos el fichero a copiar
            if (tarAux.isDirectory()) {

            }
            else {  //Una vez sabemos que es fichero lo copiamos

                salida = IOUtils.toString(tarIn);

                //DESCOMENTAR PARA GUARDAR EN FICHERO
                //FileOutputStream fileOutput = new FileOutputStream(fichero);
                //IOUtils.copy(tarIn, fileOutput);
                //fileOutput.close();
            }
        }

        tarIn.close();
        return salida;
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


        InputStream salida = new ByteArrayInputStream(bos.toByteArray());


        return salida;
    }
}
