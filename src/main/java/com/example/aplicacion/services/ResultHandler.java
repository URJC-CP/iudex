package com.example.aplicacion.services;

import com.example.aplicacion.Docker.DockerContainerCPP;
import com.example.aplicacion.Docker.DockerContainerC;
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

    @Autowired
    LanguageRepository languageRepository;

    Logger logger = LoggerFactory.getLogger(ResultHandler.class);

    private DockerClient dockerClient;
    private Map<String, String> imagenes;

    public ResultHandler() {
        logger.info("Starting connection with docker");
        this.imagenes = new HashMap<>();
        
        dockerClient = DockerClientBuilder.getInstance(getDockerURL()).build();

        //Creamos las imagenes
        /*
        File dckfl = new File("DOCKERS/Dockerfile");
        String imageId = dockerClient.buildImageCmd().withDockerfile(dckfl)
                .exec(new BuildImageResultCallback())
                .awaitImageId();
        this.imagenes.put("java", imageId);
        logger.info("Building image: "+imageId);
        System.out.println("\nhemos creado la imagen \n " + imageId);


         */
        //logger.info("hemos creado la imagen " + imageId);
        logger.info("Connection established with docker");

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
        switch (lenguaje.getNombreLenguaje()) {
            case "java":
                new DockerContainerJava(res, dockerClient, memoryLimit, timeoutTime, defaultCPU, defaultStorage).ejecutar(res.getLanguage().getImgenId());
                break;

            case "python3":
                new DockerContainerPython3(res, dockerClient, memoryLimit, timeoutTime, defaultCPU, defaultStorage).ejecutar(res.getLanguage().getImgenId());
                break;

            case "c":
                new DockerContainerC(res, dockerClient, memoryLimit, timeoutTime, defaultCPU, defaultStorage).ejecutar(res.getLanguage().getImgenId());
                break;

            case "cpp":
                new DockerContainerCPP(res, dockerClient, memoryLimit, timeoutTime, defaultCPU, defaultStorage ).ejecutar(res.getLanguage().getImgenId());
                break;

        }

    }

    public String buildImage(File file) {
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

    // returns the correct url to connect to the docker
    private String getDockerURL() {
        String osName = System.getProperty("os.name").toLowerCase();
        String dockerUrl = "";
        if (osName.startsWith("windows")) { // windows
            dockerUrl = "tcp://localhost:2375";
        } else if (osName.startsWith("linux") || osName.startsWith("mac") || osName.startsWith("unix")) { // linux, mac or unix
            dockerUrl = "unix:///var/run/docker.sock";
        } else {
            logger.error("Unsupported Operating System. There is no url for "+osName);
            throw new RuntimeException("Unsupported Operating System: "+osName);
        }
        logger.info("Running docker on: "+osName+"\nURL: "+dockerUrl);
        return dockerUrl;
    }
}
