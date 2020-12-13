package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ResultHandler;
import com.example.aplicacion.services.TeamService;
import com.example.aplicacion.services.UserService;
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
    @PostConstruct
    public void init() {

        //Creamos el lenguaje JAVA
        File dckfl = new File("DOCKERS/Java/Dockerfile");
        String imageId = resultHandler.buildImage(dckfl);
        Language lenguaje = new Language("java", imageId);
        languageRepository.save(lenguaje);

        //Creamos el lenguaje Python
        File dckfl2 = new File("DOCKERS/Python3/Dockerfile");
        String imageId2 = resultHandler.buildImage(dckfl2);
        Language lenguaje2 = new Language("python3", imageId2);
        languageRepository.save(lenguaje2);

        //Creamos el lenguaje C
        File dckfl3 = new File("DOCKERS/C/Dockerfile");
        String imageId3 = resultHandler.buildImage(dckfl3);
        Language lenguaje3 = new Language("c", imageId3);
        languageRepository.save(lenguaje3);

        userService.crearUsuario("pavloXd", "mail1");
        //userService.crearUsuario("pavloXD", "mail2");
        //userService.crearUsuario("pavloXD2", "mail1");
        //userService.deleteUserByNickname("pavloXD");

        contestService.creaContest("contestPrueba", Long.toString(teamService.getTeamByNick("pavloXd").getId()), Optional.of( "Este es el mejor concurso del mundo"));

    }
}
