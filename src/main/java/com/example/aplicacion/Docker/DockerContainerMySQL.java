package com.example.aplicacion.Docker;

import com.example.aplicacion.Entities.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import org.apache.catalina.webresources.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class DockerContainerMySQL extends DockerContainer{

    Logger logger = LoggerFactory.getLogger(DockerContainerMySQL.class);

    public DockerContainerMySQL(Result result, DockerClient dockerClient, String defaultMemoryLimit, String defaultTimeout, String defaultCPU, String defaultStorageLimit) {
        super(result, dockerClient, defaultMemoryLimit, defaultTimeout, defaultCPU, defaultStorageLimit);
    }

    public Result ejecutar(String imagenId) throws IOException {
        logger.debug("Building container for image " + imagenId);
        String defaultCPU = (this.getDefaultCPU());
        Long defaultMemoryLimit = Long.parseLong(this.getDefaultMemoryLimit());
        String defaultStorageLimit = this.getDefaultStorageLimit();

        Result result = getResult();
        String nombreClase = result.getFileName();
        String nombreDocker = "a" + result.getId() + "_" + java.time.LocalDateTime.now();
        nombreDocker = nombreDocker.replace(":", "");

        String timeout;
        if (result.getMaxTimeout() != null) {
            timeout = result.getMaxTimeout();
        } else {
            timeout = this.getDefaultTimeout();
        }

        DockerClient dockerClient = getDockerClient();
        //Creamos el contendor
        HostConfig hostConfig = new HostConfig();
        //hostConfig.withMemory(defaultMemoryLimit).withMemorySwap(defaultMemoryLimit).withStorageOpt(Map.ofEntries(Map.entry("size", defaultStorageLimit))).withCpusetCpus(defaultCPU);
        hostConfig.withMemory(defaultMemoryLimit).withCpusetCpus(defaultCPU);

        //Preparamos el script
        FileWriter fr = new FileWriter(new File("entrada.sql"));
        //generar script parar preparar el estado inicial de la bbdd
        String sqlScript = generarScriptSQL();
        fr.write(sqlScript);
        fr.close();

        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).withNetworkDisabled(true).withEnv("EXECUTION_TIMEOUT=" + result.getMaxTimeout(), "FILENAME1=" + nombreClase, "FILENAME2=entrada").withHostConfig(hostConfig).withName(nombreDocker).exec();
        logger.debug("DOCKER MySQL: Container built for result" + result.getId() + " with timeout " + result.getMaxTimeout() + " and memory limit " + result.getMaxMemory());

        //Copiamos el codigo
        copiarArchivoAContenedor(container.getId(), nombreClase + ".sql", result.getCodigo(), "/root");

        //Copiamos la entrada
        copiarArchivoAContenedor(container.getId(), "entrada.sql", result.getEntrada(), "/root");

        //Arrancamos el docker
        dockerClient.startContainerCmd(container.getId()).exec();
        //comprueba el estado del contenedor y no sigue la ejecucion hasta que este esta parado
        InspectContainerResponse inspectContainerResponse = null;
        do {
            inspectContainerResponse = dockerClient.inspectContainerCmd(container.getId()).exec();
        } while (inspectContainerResponse.getState().getRunning());  //Mientras esta corriendo se hace el do

        //Buscamos la salida Estandar
        String salidaEstandar = null;
        salidaEstandar = copiarArchivoDeContenedor(container.getId(), "root/salidaEstandar.ans");
        result.setSalidaEstandar(salidaEstandar);

        //buscamos la salida Error
        String salidaError = null;
        salidaError = copiarArchivoDeContenedor(container.getId(), "root/salidaError.ans");
        result.setSalidaError(salidaError);

        String signalEjecutor = null;
        signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        result.setSignalEjecutor(signalEjecutor);

        //logger.info("DOCKER MySQL: EL result "+result.getId() + " ha terminado con senyal "+ signal);

        dockerClient.removeContainerCmd(container.getId()).withRemoveVolumes(true).exec();

        logger.debug("DOCKER MySQL: Finished running container for result " + result.getId() + " ");
        return result;
    }

    private String generarScriptSQL() throws IOException {
        StringBuilder sqlInstructions = new StringBuilder();

        //crear tabla vacia
        sqlInstructions.append("CREATE DATABASE test IF NOT EXISTS;\n");
        sqlInstructions.append("USE test;\n");
        sqlInstructions.append("CREATE TABLE ").append(getResult().getFileName()).append(" IF NOT EXISTS\n");
        sqlInstructions.append("TRUNCATE TABLE ").append(getResult().getFileName());

        // leer entrada.in
        FileWriter fwr = new FileWriter("entrada.sql");
        BufferedReader br = new BufferedReader(new FileReader("entrada.in"));
        String[] columns = null;
        String[] values = null;

        int row = 0;

        String line = br.readLine();
        // obtener el nombre de las columnas, la primera linea de entrada.in
        if(line != null){
            row++;
            columns = line.split("\\s+");
            line = br.readLine();
        }

        if(columns == null || columns.length == 0){
            logger.warn("Couldn't create tables, first line is empty");
        }

        while(line != null){
            row++;
            // generar script sql para las tablas
            values = line.split("\\s+");
            if(values == null || values.length == 0){
                logger.warn("Couldn't create row, empty line "+row);
            }
            else if(columns.length != values.length){
                logger.warn("Couldn't create row, number of columns and values don't match in line "+row);
            }
            else{
                // añadir valores
                sqlInstructions.append("INSERT INTO ").append(getResult().getFileName()).append(" (");
                for (int i = 0; i < columns.length; i++){
                    if(i > 0) sqlInstructions.append(", ");
                    sqlInstructions.append("'"+columns[i]+"'");
                }
                sqlInstructions.append(")\nVALUES ( ");
                for (int i = 0; i < values.length; i++){
                    if(i > 0) sqlInstructions.append(", ");
                    sqlInstructions.append("'"+values[i]+"'");
                }
                sqlInstructions.append(");\n");
            }
            line = br.readLine();
        }

        br.close();
        return sqlInstructions.toString();
    }
}
