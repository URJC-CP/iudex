package es.urjc.etsii.grafo.iudex.services;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import es.urjc.etsii.grafo.iudex.docker.DockerContainerFactory;
import es.urjc.etsii.grafo.iudex.entities.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

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

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().withDockerHost(getDockerURL()).build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        dockerClient = DockerClientImpl.getInstance(config, httpClient);

        logger.info("Connection established with docker");
    }

    public void ejecutor(Result res) throws IOException, InterruptedException {
        DockerContainerFactory.createDockerContainerForResult(res, dockerClient)
                .ejecutar(res, memoryLimit, timeoutTime, defaultCPU, res.getLanguage().getImgenId());
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
        String dockerUrl;
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
