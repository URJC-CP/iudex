package com.example.aplicacion.services;

import com.example.aplicacion.Docker.DockerGeneralExecutor;
import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Repository.LanguageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



//Clase que maneja la entrada de respuestas y llama al tipo de docker correspondiente
@Service
public class ResultHandler {

    @Autowired
    LanguageRepository languageRepository;

    Logger logger = LoggerFactory.getLogger(ResultHandler.class);


    private DockerClient dockerClient;
    private Map<String, String> imagenes;
    public ResultHandler(){
        //arrancamos la conexion docker
        this.imagenes = new HashMap<>();
        dockerClient = DockerClientBuilder.getInstance("unix:///var/run/docker.sock").build();

        //Creamos las imagenes
        /*
        File dckfl = new File("DOCKERS/Dockerfile");
        String imageId = dockerClient.buildImageCmd().withDockerfile(dckfl)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
        this.imagenes.put("java", imageId);
        System.out.println("\nhemos creado la imagen \n " + imageId);


         */
        //logger.info("hemos creado la imagen " + imageId);

    }

    @Value("${docker.dockerfile.timeout}")
    private String timeoutTime;
    public void ejecutorJava(Result res){

        try {
            new DockerGeneralExecutor(res, dockerClient, timeoutTime).ejecutar(res.getLanguage().getImgenId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Conenedor terminado");

    }
    public String buildImage(File file){
        String salida = dockerClient.buildImageCmd().withDockerfile(file)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
        return salida;
    }

    public DockerClient getDockerClient() {
        return dockerClient;
    }

    public void setDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }
}
