package com.example.aplicacion.services;

import com.example.aplicacion.Docker.DockerContainerJava;
import com.example.aplicacion.Docker.DockerContainerPython3;
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

    @Value("${problem.default.timeout}")
    private String timeoutTime;
    @Value("${problem.default.memory}")
    private String memoryLimit;
    @Value("${problem.default.cores}")
    private String defaultCPU;
    @Value("${problem.default.storage}")
    private String defaultStorage;
    public void ejecutor(Result res) throws IOException {


        Language lenguaje = res.getLanguage();
        switch (lenguaje.getNombreLenguaje()){
            case "java":
                new DockerContainerJava(res, dockerClient, memoryLimit, timeoutTime, defaultCPU, defaultStorage).ejecutar(res.getLanguage().getImgenId());
                break;

            case"python3":
                new DockerContainerPython3(res, dockerClient, memoryLimit, timeoutTime, defaultCPU, defaultStorage ).ejecutar(res.getLanguage().getImgenId());
                break;



        }

        //System.out.println("Conenedor terminado");

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
