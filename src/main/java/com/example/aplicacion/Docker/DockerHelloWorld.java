package com.example.aplicacion.Docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;

//Clase incial para hacer pruebas TOCA BORRARLA EN UN FUTURO
public class DockerHelloWorld {

    private static DockerClient dockerClient;
    private File entrada;
    private File codigo;
    private String salidaEstandar;
    private String salidaError;
    private String salidaCompilador;
    private File dockerFile;

    public DockerHelloWorld() {

        //Creamos la comunicacion con el docker
        dockerClient = DockerClientBuilder.getInstance("unix:///var/run/docker.sock").build();


        //#Construir imagen
        //docker build -t pavlo/pavlo .;
        File dckfl = new File("DOCKERS/Python3/Dockerfile");
        String imageId = dockerClient.buildImageCmd().withDockerfile(dckfl).exec(new BuildImageResultCallback()).awaitImageId();

        //#Crear contenedor
        //docker create --name cont pavlo/pavlo;

        CreateContainerResponse container = dockerClient.createContainerCmd(imageId).exec();
        //System.out.println(container.getId()+"");

        String cont1 = "import java.util.Scanner;\n" + "\n" + "public class codigo {\n" + "\n" + "    public static void main(String[] args) {\n" + "        // Prints \"Hello, World\" to the terminal window.\n" + "        //System.out.println(\"HOLA, mundo\");\n" + "\n" + "        Scanner sc = new Scanner(System.in);\n" + "\n" + "        while(sc.hasNext()){\n" + "            int n = sc.nextInt();\n" + "            System.out.println(n*2);\n" + "        }\n" + "        throw new RuntimeException(\"ERROR PROVOCADO\");\n" + "    }\n" + "}";

        //#Copiar codigo
        //docker cp codigo.java cont:/root;
        //withHostResouces eliges el fichero a copiar with RometePath, donde lo vas a copiar
        try {
            copiarArchivoAContenedor(container.getId(), "codigo.java", cont1, "/root");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String cont2 = "1\n" + "2\n" + "3\n" + "8";
        //#Copiar entrada
        //docker cp entrada.txt cont:/root;
        try {
            copiarArchivoAContenedor(container.getId(), "entrada.in", cont2, "/root");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //#Arrancar contenedor
        //docker start cont;
        dockerClient.startContainerCmd(container.getId()).exec();

        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        InspectContainerResponse inspectContainerResponse = null;
        do {
            inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        } while (inspectContainerResponse.getState().getRunning());  //Mientras esta corriendo se hace el do


        //#Copiar salida Estandar
        //docker cp cont:/root/salidaEstandar.txt .;
        String salidaEstandar = null;
        try {
            salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(salidaEstandar);

        /*
        File s = new File("salidaEstandar.ans");
        BufferedWriter  salidaEstandar = null;
        //Obtenemos el InputStream del contenedor con salidaEstandar.ans
        InputStream ioEstandar = dockerClient.copyArchiveFromContainerCmd(container.getId(), "root/salidaEstandar.ans").exec();
        try{
            InputStream isSalidaEstandar=dockerClient.copyArchiveFromContainerCmd(container.getId(), "root/salidaEstandar.ans").exec();
            TarArchiveInputStream tarArchivo = new TarArchiveInputStream(isSalidaEstandar);
            convertirTarFile(tarArchivo, new File("DOCKERS/salidaEstandar.ans"));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
         */

        //#Copiar salida error
        //docker cp cont:/root/salidaError.txt .;
        String salidaError = null;
        try {
            salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(salidaError);
        //#Copiar salida compilador
        //docker cp cont:/root/salidaCompilador.txt .;

        String salidaCompilador = null;
        try {
            salidaCompilador = copiarArchivoDeContenedor(container.getId(), "root/salidaCompilador.ans");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(salidaCompilador);
        //#Borrar contenedor
        //docker rm cont;

        //System.out.println("Final");


    }

    private static void copiarArchivoAContenedor(String contAux, String nombre, String contenido, String pathDestino) throws IOException {

        //CompressArchiveUtil.tar
        //InputStream ioAux = new ByteArrayInputStream(sAux.getBytes(Charset.forName("UTF-8")));
        //new TarArchiveInputStream(ioAux).;
        dockerClient.copyArchiveToContainerCmd(contAux).withTarInputStream(convertStringtoInputStream(nombre, contenido)).withRemotePath(pathDestino).exec();

        //dockerClient.copyArchiveToContainerCmd(contAux).withHostResource(pathOrigen).withRemotePath(pathDestino).exec();

    }

    //sacado de aqui https://github.com/docker-java/docker-java/issues/991
    private static String copiarArchivoDeContenedor(String contAux, String pathOrigen) throws IOException {

        InputStream isSalida = dockerClient.copyArchiveFromContainerCmd(contAux, pathOrigen).exec();  //Obtenemos el InputStream del contenedor
        TarArchiveInputStream tarArchivo = new TarArchiveInputStream(isSalida);                     //Obtenemos el tar del IS
        return convertirTarFile(tarArchivo);                                                        //Lo traducimos

    }

    //Funcion que convierte un tar, lo guarda en fichero y devuelve un String
    private static String convertirTarFile(TarArchiveInputStream tarIn) throws IOException {
        TarArchiveEntry tarAux = null;
        String salida = null;

        while ((tarAux = tarIn.getNextTarEntry()) != null) {
            //Buscamos el fichero a copiar
            if (tarAux.isDirectory()) {

            } else {  //Una vez sabemos que es fichero lo copiamos

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
