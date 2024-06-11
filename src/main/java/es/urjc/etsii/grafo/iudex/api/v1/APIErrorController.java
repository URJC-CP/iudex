package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.pojos.ErrorAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.ResponseEntity.status;

@RestControllerAdvice
public class APIErrorController {
    private static final Logger logger = LoggerFactory.getLogger(APIErrorController.class);

    @ExceptionHandler
    public ResponseEntity<ErrorAPI> handleAccessDeniedException(AccessDeniedException e, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return errorResponse(HttpStatus.UNAUTHORIZED, e);
        } else {
            return errorResponse(HttpStatus.FORBIDDEN, e);
        }
    }

    @ExceptionHandler
    public ResponseEntity<ErrorAPI> handleError(RuntimeException e) {
        logger.error(e.toString(), e);
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    public ResponseEntity<ErrorAPI> errorResponse(HttpStatus code, Throwable t){
        var error = new ErrorAPI(t);
        return status(code).body(error);
    }
}