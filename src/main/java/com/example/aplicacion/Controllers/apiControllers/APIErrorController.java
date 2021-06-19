package com.example.aplicacion.Controllers.apiControllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APIErrorController {
    Logger logger = LoggerFactory.getLogger(APIErrorController.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(RuntimeException e) {
        logger.error(e.toString());
        return "ERROR GENERAL DEL SISTEMA";
    }
}