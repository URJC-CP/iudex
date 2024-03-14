package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.docker.DockerService;
import es.urjc.etsii.grafo.iudex.entities.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TimeZone;

@Component
@Service
public class OnStartRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(OnStartRunner.class);

    private final ContestService contestService;
    private final UserAndTeamService userAndTeamService;

    private final LanguageService languageService;

    private final DockerService docker;

    public OnStartRunner(DockerService docker,
                         LanguageService languageService,
                         UserAndTeamService userAndTeamService,
                         ContestService contestService) {
        this.docker = docker;
        this.languageService = languageService;
        this.userAndTeamService = userAndTeamService;
        this.contestService = contestService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createLanguage("java", "DOCKERS/Java/Dockerfile");
        createLanguage("python3", "DOCKERS/Python3/Dockerfile");
        createLanguage("c", "DOCKERS/C/Dockerfile");
        createLanguage("cpp", "DOCKERS/CPP/Dockerfile");
        createLanguage("sql", "DOCKERS/MySQL/Dockerfile");

        if (!userAndTeamService.existsUserByNickname("pavloXd")) {
            userAndTeamService.crearUsuario("pavloXd", "mail1");
        }
        var teamId = Long.toString(userAndTeamService.getTeamByNick("pavloXd").orElseThrow().getId());

        long startDateTime = LocalDateTime.now().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        long endDateTime = LocalDateTime.now().plusDays(1).atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
        if (contestService.existsContestByName("contestPrueba")) {
            logger.info("Demo contest contestPrueba already exists, skipping creation");
        } else {
            logger.info("Creating demo contest 'contestPrueba' with team 'pavloXd'");
            contestService.creaContest("contestPrueba", teamId, Optional.of("Este es el mejor concurso del mundo"), startDateTime, endDateTime);
            var contestId = contestService.getContestByName("contestPrueba").orElseThrow().getId();
            contestService.addTeamToContest(String.valueOf(contestId), new String[]{teamId});
        }
    }

    public void createLanguage(String name, String path) {
        // Check if language exists in DB
        logger.info("Checking language {} ...", name);
        File dockerFile = new File(path);
        var lang = languageService.getLanguageByName(name);
        String imageId;
        if (lang.isEmpty()) {
            imageId = docker.buildImage(dockerFile);
            logger.info("Created language and image {} for lang {}, Dockerfile: {}", name, imageId, dockerFile.getAbsolutePath());
            languageService.saveLanguage(new Language(name, imageId));
        } else {
            // Lang exists, check if image exists
            var newLang = lang.get();
            imageId = newLang.getImgenId();
            if (!docker.imageExists(imageId)) {
                var oldImageId = imageId;
                imageId = docker.buildImage(dockerFile);
                newLang.setImgenId(imageId);
                languageService.saveLanguage(newLang);
                logger.info("Language {} uses non-existent image {}, replacing with new image {}. Dockerfile: {}", name, oldImageId, imageId, dockerFile.getAbsolutePath());
            }
        }
    }
}
