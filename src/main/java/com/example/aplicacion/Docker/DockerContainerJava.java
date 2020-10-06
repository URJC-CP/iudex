package com.example.aplicacion.Docker;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.TheJudgeApplication;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Clase que se encarga de lanzar los docker de tipo JAVA
public class DockerContainerJava extends DockerContainer {

    Logger logger = LoggerFactory.getLogger(DockerContainerJava.class);

    public DockerContainerJava(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit){
        super(result, dockerClient, defaultMemoryLimit, defaultTimeout, defaultCPU, defaultStorageLimit);
    }



    public Result ejecutar(String imagenId) throws IOException {

        Long defaultCPU = Long.parseLong(this.getDefaultCPU());
        Long defaultMemoryLimit = Long.parseLong(this.getDefaultMemoryLimit());
        Long defaultStorageLimit = Long.parseLong(this.getDefaultStorageLimit());


        String nombreClase = getClassName();
        Result result = getResult();
        String nombreDocker = "a"+Long.toString(result.getId())+"_"+java.time.LocalDateTime.now();
        nombreDocker =nombreDocker.replace(":", "");
        String timeout;
        if(result.getMaxTimeout()!=null){
            timeout= result.getMaxTimeout();
        }
        else {
             timeout = this.getDefaultTimeout();
        }

        DockerClient dockerClient = getDockerClient();
        //Creamos el contendor
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(defaultMemoryLimit).withCpuCount(defaultCPU)
                .withDiskQuota(defaultStorageLimit);
        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).withEnv("EXECUTION_TIMEOUT="+timeout,"FILENAME1="+nombreClase, "FILENAME2="+getClassName(), "MEMORYLIMIT="+"-Xmx"+result.getMaxMemory()+"m" ).withHostConfig(hostConfig).withName(nombreDocker).exec();

        logger.info("DOCKERJAVA: Se crea el container para el result" + result.getId() + " con un timeout de " + result.getMaxTimeout() + " Y un memorylimit de "+ result.getMaxMemory());

        //Copiamos el codigo

        copiarArchivoAContenedor(container.getId(), nombreClase+".java", result.getCodigo(),  "/root");

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
        //System.out.println(time);
        result.setSalidaTime(time);


        String signalEjecutor=null;
        signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        result.setSignalEjecutor(signalEjecutor);

        String signalCompilador=null;
        signalCompilador = copiarArchivoDeContenedor(container.getId(), "root/signalCompilador.txt");
        result.setSignalCompilador(signalCompilador);

        //logger.info("DOCKERJAVA: EL result "+result.getId() + " ha terminado con senyal "+ signal);

        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        logger.info("DOCKERJAVA: Se termina el result "+ result.getId() + " ");
        return result;
    }
    private String getClassName(){
        String salida="";
        //Primero buscamos si existe una classe tipo "public class.."
        Pattern p = Pattern.compile("public\\s+class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)");
        Matcher m = p.matcher(getResult().getCodigo());
        if(m.find()){
            salida = m.group(1);

        }
        //Si no, buscamos la clase q no es publica
        else{
            Pattern p2 = Pattern.compile("class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)");
            Matcher m2 = p2.matcher(getResult().getCodigo());
            if(m2.find()){
                salida = m2.group(1);

            }
        }

        return salida;
    }
}
