package es.urjc.etsii.grafo.iudex.exceptions;

public class UnacceptedLanguageException extends RuntimeException {

    public UnacceptedLanguageException(String errorMessage) {
        super(errorMessage.equals("") ? "Language is not accepted" : errorMessage);
    }

}
