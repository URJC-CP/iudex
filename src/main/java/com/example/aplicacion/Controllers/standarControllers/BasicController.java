package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.LanguageRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.SubmissionRepository;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ResultHandler;
import com.example.aplicacion.services.TeamService;
import com.example.aplicacion.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Optional;

@Controller
public class BasicController {
    @Autowired
    public SubmissionRepository submissionRepository;
    @Autowired
    public ProblemRepository problemRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ResultHandler resultHandler;
    @Autowired
    private InNOutRepository inNOutRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private TeamService teamService;

    Logger logger = LoggerFactory.getLogger(BasicController.class);

    @PostConstruct
    public void init() {
        logger.info("Empezando creacion de la imagen del lenguaje java");
        //Creamos el lenguaje JAVA
        File dckfl = new File("DOCKERS/Java/Dockerfile");
        String imageId = resultHandler.buildImage(dckfl);
        Language lenguaje = new Language("java", imageId);
        languageRepository.save(lenguaje);
        logger.info("Creacion de la imagen del lenguaje java terminado");

        //Creamos el lenguaje Python
        logger.info("Empezando creacion de la imagen del lenguaje python");
        File dckfl2 = new File("DOCKERS/Python3/Dockerfile");
        String imageId2 = resultHandler.buildImage(dckfl2);
        Language lenguaje2 = new Language("python3", imageId2);
        languageRepository.save(lenguaje2);
        logger.info("Creacion de la imagen del lenguaje python terminado");

        //Creamos el lenguaje C
        logger.info("Empezando creacion de la imagen del lenguaje c");
        File dckfl3 = new File("DOCKERS/C/Dockerfile");
        String imageId3 = resultHandler.buildImage(dckfl3);
        Language lenguaje3 = new Language("c", imageId3);
        languageRepository.save(lenguaje3);
        logger.info("Creacion de la imagen del lenguaje c terminado");


        //Creamos el lenguaje CPP
        logger.info("Empezando creacion de la imagen del lenguaje c++");
        File dckfl4 = new File("DOCKERS/CPP/Dockerfile");
        String imageId4 = resultHandler.buildImage(dckfl4);
        Language lenguaje4 = new Language("cpp", imageId4);
        languageRepository.save(lenguaje4);
        logger.info("Creacion de la imagen del lenguaje c++ terminado");

        userService.crearUsuario("pavloXd", "mail1");
        //userService.crearUsuario("pavloXD", "mail2");
        //userService.crearUsuario("pavloXD2", "mail1");
        //userService.deleteUserByNickname("pavloXD");

        logger.info("Empezando creacion del concurso");
        contestService.creaContest("contestPrueba", Long.toString(teamService.getTeamByNick("pavloXd").getId()), Optional.of("Este es el mejor concurso del mundo"));
        logger.info("Creacion del concurso terminado");
    }
}
