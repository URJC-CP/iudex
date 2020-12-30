package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Repository.ProblemDataRepository;
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
    Logger logger = LoggerFactory.getLogger(BasicController.class);
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ResultHandler resultHandler;
    @Autowired
    private ProblemDataRepository inNOutRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private TeamService teamService;

    @PostConstruct
    public void init() {
        logger.info("Build Java image ");
        File dckfl = new File("DOCKERS/Java/Dockerfile");
        String imageId = resultHandler.buildImage(dckfl);
        Language lenguaje = new Language("java", imageId);
        languageRepository.save(lenguaje);
        logger.info("Finish build Java image " + imageId + " from " + dckfl.getName());

        //Creamos el lenguaje Python
        logger.info("Build Python image");
        File dckfl2 = new File("DOCKERS/Python3/Dockerfile");
        String imageId2 = resultHandler.buildImage(dckfl2);
        Language lenguaje2 = new Language("python3", imageId2);
        languageRepository.save(lenguaje2);
        logger.info("Finish build Python image " + imageId2 + " from " + dckfl2.getName());

        //Creamos el lenguaje C
        logger.info("Build C image");
        File dckfl3 = new File("DOCKERS/C/Dockerfile");
        String imageId3 = resultHandler.buildImage(dckfl3);
        Language lenguaje3 = new Language("c", imageId3);
        languageRepository.save(lenguaje3);
        logger.info("Finish build C image " + imageId3 + " from " + dckfl3.getName());

        //Creamos el lenguaje CPP
        logger.info("Build C++ image");
        File dckfl4 = new File("DOCKERS/CPP/Dockerfile");
        String imageId4 = resultHandler.buildImage(dckfl4);
        Language lenguaje4 = new Language("cpp", imageId4);
        languageRepository.save(lenguaje4);
        logger.info("Finish build C++ image " + imageId4 + " from " + dckfl4.getName());

        userService.crearUsuario("pavloXd", "mail1");
        //userService.crearUsuario("pavloXD", "mail2");
        //userService.crearUsuario("pavloXD2", "mail1");
        //userService.deleteUserByNickname("pavloXD");

        contestService.creaContest("contestPrueba", Long.toString(teamService.getTeamByNick("pavloXd").get().getId()), Optional.of("Este es el mejor concurso del mundo"));
    }
}
