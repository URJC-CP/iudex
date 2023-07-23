package es.urjc.etsii.grafo.iudex.exceptions;

public class DockerExecutionException extends RuntimeException {

    public DockerExecutionException() {
    }

    public DockerExecutionException(String message) {
        super(message);
    }

    public DockerExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public DockerExecutionException(Throwable cause) {
        super(cause);
    }

}
