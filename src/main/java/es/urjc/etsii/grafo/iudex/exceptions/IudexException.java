package es.urjc.etsii.grafo.iudex.exceptions;

public class IudexException extends RuntimeException {

    public IudexException(String errorMessage) {
        super(errorMessage.equals("") ? "An iudex exception has occurred" : errorMessage);
    }

}