package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Repository.LanguageRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.SubmissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

@Component
public class OnStartRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(OnStartRunner.class);

    @Autowired
    public SubmissionRepository submissionRepository;
    @Autowired
    public ProblemRepository problemRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ResultHandler resultHandler;
    @Autowired
    private UserService userService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private TeamService teamService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        createLanguage("java", "DOCKERS/Java/Dockerfile");
        createLanguage("python3", "DOCKERS/Python3/Dockerfile");
        createLanguage("c", "DOCKERS/C/Dockerfile");
        createLanguage("cpp", "DOCKERS/CPP/Dockerfile");
        createLanguage("sql", "DOCKERS/MySQL/Dockerfile");

        userService.crearUsuario("pavloXd", "mail1");
        var teamId = Long.toString(teamService.getTeamByNick("pavloXd").orElseThrow().getId());

        long startDateTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        long endDateTime = LocalDateTime.now().plusDays(1).atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        if (contestService.existsContestByName("contestPrueba")) {
            logger.info("Demo contest contestPrueba already exists, skipping creation");
        } else {
            logger.info("Creating demo contest 'contestPrueba' with team 'pavloXd'");
            contestService.creaContest("contestPrueba", teamId, Optional.of("Este es el mejor concurso del mundo"), startDateTime, endDateTime);
        }
    }

    public void createLanguage(String name, String path) {
        if (languageRepository.existsLanguageByNombreLenguaje(name)) {
            logger.info(String.format("Skipping creation of lang %s, already in Database", name));
            return;
        }
        logger.info(String.format("Building %s image at path %s", name, path));
        File dockerFile = new File(path);
        String imageId = resultHandler.buildImage(dockerFile);
        Language language = new Language(name, imageId);
        languageRepository.save(language);
        logger.info(String.format("Finished building %s image %s from %s", name, imageId, dockerFile.getName()));
    }
}
