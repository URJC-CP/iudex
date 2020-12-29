package com.example.aplicacion.smoketest;

import com.example.aplicacion.Controllers.apiControllers.APIContestController;
import com.example.aplicacion.Controllers.apiControllers.APIProblemController;
import com.example.aplicacion.Controllers.apiControllers.APISubmissionController;
import com.example.aplicacion.Controllers.apiControllers.APITeamController;
import com.example.aplicacion.Controllers.standarControllers.BasicController;
import com.example.aplicacion.Controllers.standarControllers.ContestController;
import com.example.aplicacion.Controllers.standarControllers.IndiceController;
import com.example.aplicacion.Controllers.standarControllers.ProblemController;
import com.example.aplicacion.Repository.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionReceiver;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import com.example.aplicacion.rabbitMQ.RabbitResultRevieserReceiver;
import com.example.aplicacion.rabbitMQ.RabbitResultReviserSender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest {
    //APIControllers
    @Autowired
    APIContestController apiContestController;
    @Autowired
    APIProblemController apiProblemController;
    @Autowired
    APISubmissionController apiSubmissionController;
    @Autowired
    APITeamController apiTeamController;

    // controllers
    @Autowired
    BasicController basicController;
    @Autowired
    ContestController contestController;
    @Autowired
    IndiceController indiceController;
    @Autowired
    ProblemController problemController;

    //repositories
    @Autowired
    ContestRepository contestRepository;
    @Autowired
    InNOutRepository problemDataRepository;
    @Autowired
    LanguageRepository languageRepository;
    @Autowired
    ProblemRepository problemRepository;
    @Autowired
    ResultRepository resultRepository;
    @Autowired
    SubmissionRepository submissionRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    UserRepository userRepository;

    //rabbit
    @Autowired
    RabbitResultReviserSender reviserSender;
    @Autowired
    RabbitResultExecutionSender sender;
    @Autowired
    RabbitResultRevieserReceiver reviserReceiver;
    @Autowired
    RabbitResultExecutionReceiver receiver;

    // verificar que la app este desplegada
    @Test
    public void contextLoads() {
        //check rabbit
        Assertions.assertNotNull(reviserReceiver);
        Assertions.assertNotNull(receiver);
        Assertions.assertNotNull(sender);
        Assertions.assertNotNull(reviserSender);

        //check api controllers have been initialized
        Assertions.assertNotNull(apiContestController);
        Assertions.assertNotNull(apiProblemController);
        Assertions.assertNotNull(apiSubmissionController);
        Assertions.assertNotNull(apiTeamController);

        //check controllers have been initialized
        Assertions.assertNotNull(basicController);
        Assertions.assertNotNull(contestController);
        Assertions.assertNotNull(indiceController);
        Assertions.assertNotNull(problemController);

        //check repositories have been initialized
        Assertions.assertNotNull(contestRepository);
        Assertions.assertNotNull(problemDataRepository);
        Assertions.assertNotNull(languageRepository);
        Assertions.assertNotNull(problemRepository);
        Assertions.assertNotNull(resultRepository);
        Assertions.assertNotNull(submissionRepository);
        Assertions.assertNotNull(teamRepository);
        Assertions.assertNotNull(userRepository);
    }

    // verificar estado inicial de los repositorios
    @Test
    public void emptyRepositories() {
        Assertions.assertEquals(contestRepository.findAll().size(), 1);
        Assertions.assertTrue(problemDataRepository.findAll().isEmpty());
        Assertions.assertFalse(languageRepository.findAll().isEmpty());
        Assertions.assertTrue(problemRepository.findAll().isEmpty());
        Assertions.assertTrue(resultRepository.findAll().isEmpty());
        Assertions.assertTrue(submissionRepository.findAll().isEmpty());
        Assertions.assertEquals(teamRepository.findAll().size(), 1);
        Assertions.assertEquals(userRepository.findAll().size(), 1);
    }

    // verificar los lenguajes
    @Test
    public void languageTest() {
        Assertions.assertNotNull(languageRepository.findLanguageByNombreLenguaje("java"));
        Assertions.assertNotNull(languageRepository.findLanguageByNombreLenguaje("python"));
        Assertions.assertNotNull(languageRepository.findLanguageByNombreLenguaje("c"));
        Assertions.assertNotNull(languageRepository.findLanguageByNombreLenguaje("cpp"));
        Assertions.assertNotNull(languageRepository.findLanguageByNombreLenguaje("sql"));
    }

    // verificar el paso de mensajes por la cola -- rabbit
    @Test
    public void testSend() {

    }

    @Test
    public void testReceive() {

    }


}