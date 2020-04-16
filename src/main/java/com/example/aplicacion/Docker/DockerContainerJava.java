package com.example.aplicacion.Docker;

import com.example.aplicacion.Entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

//Clase que se encarga de lanzar los docker de tipo JAVA
public class DockerContainerJava extends DockerContainer {



    public DockerContainerJava(Result result, DockerClient dockerClient, String timeoutTime){
        super(result, dockerClient, timeoutTime);
    }



    public Result ejecutar(String imagenId) throws IOException {

        Result result = getResult();
        DockerClient dockerClient = getDockerClient();
        String timeoutTime = getTimeoutTime();
        //Creamos el contendor
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100000000L).withCpuPercent(100L);
        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).withEnv("EXECUTION_TIMEOUT="+timeoutTime,"FILENAME="+result.getFileName() ).withHostConfig(hostConfig).exec();


        //Copiamos el codigo

        copiarArchivoAContenedor(container.getId(), result.getFileName()+".java", result.getCodigo(),  "/root");

        //Copiamos la entrada

        copiarArchivoAContenedor(container.getId(), "entrada.in", result.getEntrada(), "/root");

        //Arrancamos el docker
        dockerClient.startContainerCmd(container.getId()).exec();
        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        InspectContainerResponse inspectContainerResponse=null;
        do {
            inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        }while (inspectContainerResponse.getState().getRunning());  //Mientras esta corriendo se hace el do


        //Buscamos la salida Estandar
        String salidaEstandar=null;
        salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");

        //System.out.println(salidaEstandar);
        result.setSalidaEstandar(salidaEstandar);

        //buscamos la salida Error
        String salidaError=null;

        salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");

        //System.out.println(salidaError);
        result.setSalidaError(salidaError);

        //buscamos la salida Compilador
        String salidaCompilador=null;
        salidaCompilador = copiarArchivoDeContenedor(container.getId(), "root/salidaCompilador.ans");
        //System.out.println(salidaCompilador);
        result.setSalidaCompilador(salidaCompilador);

        String time= null;
        time = copiarArchivoDeContenedor(container.getId(), "root/time.txt");
        System.out.println(time);
        result.setSalidaTime(time);


        String timeout=null;
        timeout = copiarArchivoDeContenedor(container.getId(), "root/timeout.txt");
        System.out.println(timeout);
        result.setTimeout(timeout);



        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        return result;
    }
}
