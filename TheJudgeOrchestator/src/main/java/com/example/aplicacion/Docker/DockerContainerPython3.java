package com.example.aplicacion.Docker;

import com.example.aplicacion.Entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

//Clase que se encarga de lanzar los docker de tipo JAVA
public class DockerContainerPython3 extends DockerContainer {
    Logger logger = LoggerFactory.getLogger(DockerContainerPython3.class);



    public DockerContainerPython3(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit){
        super(result, dockerClient, defaultMemoryLimit, defaultTimeout, defaultCPU, defaultStorageLimit);
    }


    public Result ejecutar(String imagenId) throws IOException {
        String defaultCPU = (this.getDefaultCPU());
        Long defaultMemoryLimit = Long.parseLong(this.getDefaultMemoryLimit());
        String defaultStorageLimit = this.getDefaultStorageLimit();

        Result result = getResult();
        String nombreClase = result.getFileName();
        String nombreDocker = "a"+Long.toString(result.getId())+"_"+java.time.LocalDateTime.now();
        nombreDocker =nombreDocker.replace(":", "");

        DockerClient dockerClient = getDockerClient();
        //Creamos el contendor
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(defaultMemoryLimit).withMemorySwap(defaultMemoryLimit).withStorageOpt(Map.ofEntries(Map.entry("size", defaultStorageLimit))).withCpusetCpus(defaultCPU);
        //hostConfig.withMemory(defaultMemoryLimit).withCpusetCpus(defaultCPU);


        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).withEnv("EXECUTION_TIMEOUT="+result.getMaxTimeout(), "FILENAME2="+nombreClase+".py" ).withHostConfig(hostConfig).withName(nombreDocker).exec();

        logger.info("DOCKERPYTHON: Se crea el container para el result" + result.getId() + " con un timeout de " + result.getMaxTimeout() + " Y un memorylimit de "+ result.getMaxMemory());

        //Copiamos el codigo

        copiarArchivoAContenedor(container.getId(), nombreClase+".py", result.getCodigo(),  "/root");

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


        String signal=null;
        signal = copiarArchivoDeContenedor(container.getId(), "root/signal.txt");
        //System.out.println(signal);
        result.setSignalEjecutor(signal);

        //logger.info("DOCKERJAVA: EL result "+result.getId() + " ha terminado con senyal "+ signal);

        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        logger.info("DOCKERPYTHON3: Se termina el result "+ result.getId() + " ");
        return result;
    }


}
