package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.services.ResultReviser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class APIErrorController {
    Logger logger = LoggerFactory.getLogger(APIErrorController.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleError(RuntimeException e){
        logger.error(""+ e.toString());

        return "ERROR GENERAL DEL SISTEMA";
    }

}
