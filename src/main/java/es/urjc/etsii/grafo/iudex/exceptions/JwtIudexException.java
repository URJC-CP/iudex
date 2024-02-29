package es.urjc.etsii.grafo.iudex.exceptions;

public class JwtIudexException extends RuntimeException {

    public JwtIudexException(Throwable cause) {
        super(cause);
    }

    public JwtIudexException(String cause) {
        super(cause);
    }

}