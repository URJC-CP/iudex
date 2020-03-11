package com.example.aplicacion;

import Docker.DockerJava;
import Entities.Answer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Clase que maneja la entrada de respuestas y llama al tipo de docker correspondiente
public class AnswerHandler {

    private DockerClient dockerClient;
    private Map<String, String> imagenes;

    public AnswerHandler(){
        //arrancamos la conexion docker
        this.imagenes = new HashMap<>();
        dockerClient = DockerClientBuilder.getInstance("unix:///var/run/docker.sock").build();

        //Creamos las imagenes
        File dckfl = new File("DOCKERS/Dockerfile");
        String imageId = dockerClient.buildImageCmd().withDockerfile(dckfl)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
        this.imagenes.put("java", imageId);

        //Provisional apra ejecutor
        ejecutorJava(imageId);

    }

    public void ejecutorJava(String imagenId){
        String cont1 = "import java.util.Scanner;\n" +
                "\n" +
                "public class codigo {\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        // Prints \"Hello, World\" to the terminal window.\n" +
                "        //System.out.println(\"HOLA, mundo\");\n" +
                "\n" +
                "        Scanner sc = new Scanner(System.in);\n" +
                "\n" +
                "        while(sc.hasNext()){\n" +
                "            int n = sc.nextInt();\n" +
                "            System.out.println(n*2);\n" +
                "        }\n" +
                "        throw new RuntimeException(\"ERROR PROVOCADO\");\n" +
                "    }\n" +
                "}";
        String cont2 = "1\n" +
                "2\n" +
                "3\n" +
                "8";

        Answer ans = new Answer(cont1, cont2);


        try {
            new DockerJava(ans, dockerClient).ejecutar(imagenId);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
