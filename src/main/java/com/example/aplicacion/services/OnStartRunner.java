package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Language;
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
    private ResultHandler resultHandler;
    @Autowired
    private UserService userService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private LanguageService languageService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        createLanguage("java", "DOCKERS/Java/Dockerfile");
        createLanguage("python3", "DOCKERS/Python3/Dockerfile");
        createLanguage("c", "DOCKERS/C/Dockerfile");
        createLanguage("cpp", "DOCKERS/CPP/Dockerfile");
        createLanguage("sql", "DOCKERS/MySQL/Dockerfile");

        if (!userService.existsUserByNickname("pavloXd")) {
            userService.crearUsuario("pavloXd", "mail1");
        }
        String teamId = Long.toString(teamService.getTeamByNick("pavloXd").orElseThrow().getId());

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
        if (languageService.existsLanguageByName(name)) {
            logger.info("Skipping creation of lang {}, already in Database", name);
            return;
        }
        logger.info("Building {} image at path {}", name, path);
        File dockerFile = new File(path);
        String imageId = resultHandler.buildImage(dockerFile);
        Language language = new Language(name, imageId);
        languageService.saveLanguage(language);
        logger.info("Finished building {} image {} from {}", name, imageId, dockerFile.getName());
    }
}
