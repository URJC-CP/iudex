package Docker;

import com.fasterxml.jackson.core.json.UTF8DataInputJsonParser;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
//para convertir de stdin a string
//import com.github.dockerjava.utils.TestUtils;


import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class DockerHelloWorld {

    public DockerHelloWorld ()  {

        //Creamos la comunicacion con el docker
        DockerClient dockerClient = DockerClientBuilder.getInstance("unix:///var/run/docker.sock").build();
        //List<Container> containers = dockerClient.listContainersCmd().exec();
        //dockerClient.copyArchiveToContainerCmd(containers.get(0).getId()).withHostResource("/home/pavlo/IdeaProjects/TheJudge/DOCKERS/codigo.java").withRemotePath("/root").exec();

        //Obtenemos la info del cliente para ver que funciona.
        //Info info = dockerClient.infoCmd().exec();
        //System.out.print(info);


        //List<Container> containers = dockerClient.listContainersCmd().exec();        //crear contenedor
        //CreateContainerResponse container= dockerClient.createContainerCmd("openjdk:alpine").withName("container2").withCmd("bash").exec();
        //dockerClient.startContainerCmd(container.getId());


        //#Construir imagen
        //docker build -t pavlo/pavlo .;
        File dckfl = new File("DOCKERS/Dockerfile");
        String imageId = dockerClient.buildImageCmd().withDockerfile(dckfl)
                .exec(new BuildImageResultCallback())
                .awaitImageId();

        //#Crear contenedor
        //docker create --name cont pavlo/pavlo;

        CreateContainerResponse container = dockerClient.createContainerCmd(imageId).exec();
        System.out.println(container.getId()+"");



        //#Copiar codigo
        //docker cp codigo.java cont:/root;
        //withHostResouces eliges el fichero a copiar with RometePath, donde lo vas a copiar
        dockerClient.copyArchiveToContainerCmd(container.getId()).withHostResource("DOCKERS/codigo.java").withRemotePath("/root").exec();


        //#Copiar entrada
        //docker cp entrada.txt cont:/root;
       // try(InputStream uploadStream = Files.newInputStream(Path.of("salida.ans"))){

       // }
        dockerClient.copyArchiveToContainerCmd(container.getId()).withHostResource("DOCKERS/entrada.in").withRemotePath("/root").exec();


        //#Arrancar contenedor
        //docker start cont;
        //sleep 1;
        dockerClient.startContainerCmd(container.getId()).exec();
        //necesario el retraso para que no se intente copiar sin que se haya ejecutado ya el codigo
        //try {
        //    TimeUnit.MILLISECONDS.sleep(1500);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}


        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        InspectContainerResponse inspectContainerResponse=null;
        do {

             inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        }while (inspectContainerResponse.getState().getRunning());  //Mientras esta corriendo se hace el do


        //#Copiar salida Estandar
        //docker cp cont:/root/salidaEstandar.txt .;
        File s = new File("salidaEstandar.ans");

        BufferedWriter  salidaEstandar = null;
        InputStream ioEstandar = dockerClient.copyArchiveFromContainerCmd(container.getId(), "root/salidaEstandar.ans").exec();
        try {
            Boolean bytesAvailable = ioEstandar.available() > 0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        String salida = null;
        try {
            salida = convert(ioEstandar, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //String responseAsString = TestUtils.asString(response);

        //try {
        //    FileUtils.copyInputStreamToFile(ioEstandar, s);
        //} catch (IOException e) {
        //    e.printStackTrace();
       // }

        System.out.println(salida);
        System.out.println("Final");



        //#Copiar salida error
        //docker cp cont:/root/salidaError.txt .;

        //#Copiar salida error
        //docker cp cont:/root/salidaCompilador.txt .;

        //#Borrar contenedor
        //docker rm cont;

    }

    //Funcion que convierte de inputstream a string
    public String convert(InputStream inputStream, Charset charset) throws IOException {

        try (Scanner scanner = new Scanner(inputStream, charset.name())) {
            return scanner.useDelimiter("\\A").next();
        }
    }
}
