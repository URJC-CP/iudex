package com.example.aplicacion;

import com.example.aplicacion.Docker.DockerJava;
import com.example.aplicacion.Entities.Answer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;

import java.io.File;
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

    }

    public void ejecutorJava(Answer ans){

        try {
            new DockerJava(ans, dockerClient).ejecutar(imagenes.get("java"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("FINAL");

    }


}
