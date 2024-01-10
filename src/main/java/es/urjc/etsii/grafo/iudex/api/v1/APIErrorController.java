package es.urjc.etsii.grafo.iudex.api.v1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class APIErrorController {
    private static final Logger logger = LoggerFactory.getLogger(APIErrorController.class);

    @ExceptionHandler({ org.springframework.security.access.AccessDeniedException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e) {
        if (logger.isErrorEnabled()) logger.error(e.toString(), e);
        return "UNAUTHORIZED";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(RuntimeException e) {
        if (logger.isErrorEnabled()) logger.error(e.toString(), e);
        return "ERROR GENERAL DEL SISTEMA";
    }
}