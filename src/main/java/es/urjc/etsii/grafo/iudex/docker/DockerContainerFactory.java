package es.urjc.etsii.grafo.iudex.docker;

import com.github.dockerjava.api.DockerClient;
import es.urjc.etsii.grafo.iudex.entities.Result;

public class DockerContainerFactory {

    public static DockerContainer createDockerContainerForLanguage(String language, DockerClient dockerClient) {
        return switch (language) {
            case "java" -> new DockerContainerJava(dockerClient);
            case "python3" -> new DockerContainerPython(dockerClient);
            case "cpp" -> new DockerContainerCpp(dockerClient);
            case "c" -> new DockerContainerC(dockerClient);
            case "sql" -> new DockerContainerMySQL(dockerClient);
            default -> throw new IllegalArgumentException("Language not supported: " + language);
        };
    }

    public static DockerContainer createDockerContainerForResult(Result result, DockerClient dockerClient) {
        return createDockerContainerForLanguage(result.getLanguage().getNombreLenguaje(), dockerClient);
    }

}
