package Docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.ArrayList;
import java.util.List;

public class DockerHelloWorld {

    public DockerHelloWorld (){

        //Creamos la comunicacion con el docker
        DockerClient dockerClient = DockerClientBuilder.getInstance("unix:///var/run/docker.sock").build();

        //Obtenemos la info del cliente para ver que funciona.
        Info info = dockerClient.infoCmd().exec();
        //System.out.print(info);


        List<Container> containers = dockerClient.listContainersCmd().exec();        //crear contenedor
        CreateContainerResponse container= dockerClient.createContainerCmd("openjdk:alpine").withName("container2").withCmd("bash").exec();
        //dockerClient.startContainerCmd(container.getId());
    }
}
