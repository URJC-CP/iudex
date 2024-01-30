package es.urjc.etsii.grafo.iudex.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import es.urjc.etsii.grafo.iudex.entities.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DockerContainerC extends DockerContainer {

    private static final Logger logger = LoggerFactory.getLogger(DockerContainerC.class);

    public DockerContainerC(DockerClient dockerClient) {
        super(dockerClient);
    }

    @Override
    protected String getClassName(Result result) {
        return result.getFileName();
    }

    @Override
    protected String getFileExtension() {
        return "c";
    }

    @Override
    protected String getFileName1(Result result) {
        return result.getFileName();
    }

    @Override
    protected String getFileName2(Result result, String fileExtension) {
        return result.getFileName() + "." + fileExtension;
    }

    @Override
    protected void setSignals(Result result, CreateContainerResponse container) throws IOException {
        String signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        result.setSignalEjecutor(signalEjecutor);

        String signalCompilador = copiarArchivoDeContenedor(container.getId(), "root/signalCompilador.txt");
        result.setSignalCompilador(signalCompilador);
    }
}
