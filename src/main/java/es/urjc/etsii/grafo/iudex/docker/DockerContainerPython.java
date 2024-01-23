package es.urjc.etsii.grafo.iudex.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import es.urjc.etsii.grafo.iudex.entities.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DockerContainerPython extends DockerContainer {

    private static final Logger logger = LoggerFactory.getLogger(DockerContainerPython.class);

    public DockerContainerPython(DockerClient dockerClient) {
        super(dockerClient);
    }

    @Override
    protected String getClassName(Result result) {
        return result.getFileName();
    }

    @Override
    protected String getFileExtension() {
        return "py";
    }

    @Override
    protected String getFileName1(Result result) {
        return result.getFileName();
    }

    @Override
    protected String[] getEnv(Result result, String defaultTimeout) {
        List<String> env = new ArrayList<>();

        env.add("EXECUTION_TIMEOUT=" + getTimeoutEnv(result, defaultTimeout));
        env.add("FILENAME2=" + getFileName2(result, getFileExtension()));

        return env.toArray(new String[0]);
    }

    @Override
    protected String getFileName2(Result result, String fileExtension) {
        return result.getFileName() + "." + fileExtension;
    }

    @Override
    protected void setSignals(Result result, CreateContainerResponse container) throws IOException {
        String signal = copiarArchivoDeContenedor(container.getId(), "root/signal.txt");
        result.setSignalEjecutor(signal);
    }
}
