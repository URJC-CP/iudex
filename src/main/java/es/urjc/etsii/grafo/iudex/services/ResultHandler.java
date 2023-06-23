package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.docker.*;
import es.urjc.etsii.grafo.iudex.entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

//Clase que maneja la entrada de respuestas y llama al tipo de docker correspondiente
@Service
public class ResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(ResultHandler.class);

    private DockerClient dockerClient;
    @Value("${problem.default.timeout}")
    private String timeoutTime;
    @Value("${problem.default.memory}")
    private String memoryLimit;
    @Value("${problem.default.cores}")
    private String defaultCPU;
    @Value("${problem.default.storage}")
    private String defaultStorage;

    public ResultHandler() {
        logger.info("Starting connection with docker");
        dockerClient = DockerClientBuilder.getInstance(getDockerURL()).build();
        logger.info("Connection established with docker");
    }

    public void ejecutor(Result res) throws IOException, InterruptedException {
        if (res.getLanguage().getNombreLenguaje().equals("sql")) {
            new DockerContainerMySQL(dockerClient).ejecutar(res, memoryLimit, timeoutTime, defaultCPU, res.getLanguage().getImgenId());
        } else {
            new DockerContainer(dockerClient).ejecutar(res, memoryLimit, timeoutTime, defaultCPU, res.getLanguage().getImgenId());
        }
    }

    public String buildImage(File file) {
        return dockerClient.buildImageCmd().withDockerfile(file).exec(new BuildImageResultCallback()).awaitImageId();
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
            logger.error("Unsupported Operating System. {}", osName);
            throw new RuntimeException("Unsupported Operating System: " + osName);
        }
        logger.info("Running docker on {} ", osName);
        return dockerUrl;
    }
}
