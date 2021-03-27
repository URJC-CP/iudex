package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Language;

import com.example.aplicacion.services.ResultHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Optional;


@Controller
public class BasicController {

    @Autowired
    private ResultHandler resultHandler;

    @PostConstruct
    public void init() {

        //Creamos el lenguaje JAVA
        File dckfl = new File("TheJudge/DOCKERS/Java/Dockerfile");
        String imageId = resultHandler.buildImage(dckfl);
        Language lenguaje = new Language("java", imageId);

        //Creamos el lenguaje Python
        File dckfl2 = new File("TheJudge/DOCKERS/Python3/Dockerfile");
        String imageId2 = resultHandler.buildImage(dckfl2);
        Language lenguaje2 = new Language("python3", imageId2);


        



    }
}
