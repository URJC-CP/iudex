package Docker;

import Entities.Answer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//Clase que se encarga de lanzar los docker de tipo JAVA
public class DockerJava {
    private File entrada;
    private File codigo;
    private String salidaEstandar;
    private String salidaError;
    private String salidaCompilador;
    private File dockerFile;
    private Answer answer;

    private static DockerClient dockerClient;



    public  DockerJava(Answer answer, File dockerFile, DockerClient dockerClient){
        this.answer = answer;
        this.entrada = answer.getEntrada();
        this.codigo=answer.getCodigo();
        this.salidaEstandar=answer.getSalidaEstandar();
        this.salidaCompilador=answer.getSalidaCompilador();
        this.salidaError=answer.getSalidaError();

        this.dockerClient = dockerClient;

        this.dockerFile=dockerFile;

    }
    public Answer ejecutar(String imagenId){
        //Creamos el contendor
        CreateContainerResponse container = dockerClient.createContainerCmd(imagenId).exec();
        copiarArchivoAContenedor(container.getId(), answer.getCodigo(), "/root");




        return answer;
    }

    private static void copiarArchivoAContenedor (String contAux, String pathOrigen, String pathDestino ){
        dockerClient.copyArchiveToContainerCmd(contAux).withHostResource(pathOrigen).withRemotePath(pathDestino).exec();

    }

    //sacado de aqui https://github.com/docker-java/docker-java/issues/991
    private static String copiarArchivoDeContenedor (String contAux, String pathOrigen) throws IOException {

        InputStream isSalida=dockerClient.copyArchiveFromContainerCmd(contAux, pathOrigen).exec();  //Obtenemos el InputStream del contenedor
        TarArchiveInputStream tarArchivo = new TarArchiveInputStream(isSalida);                     //Obtenemos el tar del IS
        return convertirTarFile(tarArchivo);                                                        //Lo traducimos

    }

    //Funcion que convierte un tar, lo guarda en fichero y devuelve un String
    private static String convertirTarFile(TarArchiveInputStream tarIn) throws IOException {
        TarArchiveEntry tarAux = null;
        String salida=null;

        while ((tarAux = tarIn.getNextTarEntry()) != null) {
            //Buscamos el fichero a copiar
            if (tarAux.isDirectory()) {

            }
            else {  //Una vez sabemos que es fichero lo copiamos

                salida = IOUtils.toString(tarIn);

                //DESCOMENTAR PARA GUARDAR EN FICHERO
                //FileOutputStream fileOutput = new FileOutputStream(fichero);
                //IOUtils.copy(tarIn, fileOutput);
                //fileOutput.close();
            }
        }



        tarIn.close();
        return salida;
    }
}
