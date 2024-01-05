package es.urjc.etsii.grafo.iudex.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import es.urjc.etsii.grafo.iudex.entities.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DockerContainerJava extends DockerContainer {

    private static final Logger logger = LoggerFactory.getLogger(DockerContainerJava.class);

    public DockerContainerJava(DockerClient dockerClient) {
        super(dockerClient);
    }

    @Override
    protected String getClassName(Result result) {
        return this.getJavaClassName(result);
    }

    @Override
    protected String getFileExtension() {
        return "java";
    }

    @Override
    protected String getFileName1(Result result) {
        return this.getJavaClassName(result);
    }

    @Override
    protected String[] getEnv(Result result, String defaultTimeout) {
        List<String> env = new ArrayList<>();

        env.add("EXECUTION_TIMEOUT=" + getTimeoutEnv(result, defaultTimeout));
        env.add("FILENAME2=" + getFileName2(result, getFileExtension()));
        env.add("FILENAME1=" + getFileName1(result));
        env.add("MEMORYLIMIT=" + "-Xmx" + result.getMaxMemory() + "m");

        return env.toArray(new String[0]);
    }

    @Override
    protected String getFileName2(Result result, String fileExtension) {
        return this.getJavaClassName(result);
    }

    @Override
    protected void setSignals(Result result, CreateContainerResponse container) throws IOException {
        String signalEjecutor = copiarArchivoDeContenedor(container.getId(), "root/signalEjecutor.txt");
        result.setSignalEjecutor(signalEjecutor);

        String signalCompilador = copiarArchivoDeContenedor(container.getId(), "root/signalCompilador.txt");
        result.setSignalCompilador(signalCompilador);
    }

    private String getJavaClassName(Result result) {
        Matcher publicClassMatcher = Pattern.compile("public\\s+class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)")
                .matcher(result.getCodigo());
        Matcher privateClassMatcher = Pattern.compile("class\\s+([a-zA-Z_$][a-zA-Z_$0-9]*)")
                .matcher(result.getCodigo());

        if (publicClassMatcher.find()) { return publicClassMatcher.group(1); }
        else if (privateClassMatcher.find()) { return privateClassMatcher.group(1); }

        return "";
    }
}
