package com.example.aplicacion.smoketest;

import com.example.aplicacion.controllers.apiControllers.APIContestController;
import com.example.aplicacion.controllers.apiControllers.APIProblemController;
import com.example.aplicacion.controllers.apiControllers.APISubmissionController;
import com.example.aplicacion.controllers.apiControllers.APITeamController;
import com.example.aplicacion.controllers.standarControllers.ContestController;
import com.example.aplicacion.controllers.standarControllers.IndiceController;
import com.example.aplicacion.controllers.standarControllers.ProblemController;
import com.example.aplicacion.repositories.*;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import com.example.aplicacion.rabbitMQ.RabbitResultRevieserReceiver;
import com.example.aplicacion.rabbitMQ.RabbitResultReviserSender;
import com.example.aplicacion.services.OnStartRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmokeTest {
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
    ContestController contestController;
    @Autowired
    IndiceController indiceController;
    @Autowired
    ProblemController problemController;

    //repositories
    @Autowired
    ContestRepository contestRepository;
    @Autowired
    SampleRepository sampleRepository;
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

    // Runner
    @Autowired
    OnStartRunner onStartRunner;

    @Test
    @DisplayName("verificar que la app este desplegada")
    void contextLoads() {
        //check rabbit
        assertNotNull(reviserReceiver);
        assertNotNull(sender);
        assertNotNull(reviserSender);

        //check api controllers have been initialized
        assertNotNull(apiContestController);
        assertNotNull(apiProblemController);
        assertNotNull(apiSubmissionController);
        assertNotNull(apiTeamController);

        //check controllers have been initialized
        assertNotNull(contestController);
        assertNotNull(indiceController);
        assertNotNull(problemController);

        //check repositories have been initialized
        assertNotNull(contestRepository);
        assertNotNull(sampleRepository);
        assertNotNull(languageRepository);
        assertNotNull(problemRepository);
        assertNotNull(resultRepository);
        assertNotNull(submissionRepository);
        assertNotNull(teamRepository);
        assertNotNull(userRepository);

        // check application runner
        assertNotNull(onStartRunner);
    }

    // verificar estado inicial de los repositorios
    @Test
    @DisplayName("Verificar estado inicial de los repositorios")
    @Disabled("No se cumple nunca si se han ejecutado otros tests antes porque la bbdd esta sucia")
    void emptyRepositories() {
        assertEquals(1, contestRepository.findAll().size());
        assertTrue(sampleRepository.findAll().isEmpty());
        assertFalse(languageRepository.findAll().isEmpty());
        assertTrue(problemRepository.findAll().isEmpty());
        assertTrue(resultRepository.findAll().isEmpty());
        assertTrue(submissionRepository.findAll().isEmpty());
        assertEquals(1, teamRepository.findAll().size());
        assertEquals(1, userRepository.findAll().size());
    }

    // verificar los lenguajes
    @Test
    @DisplayName("Verificar los lenguajes soportados")
    void languageTest() {
        assertNotNull(languageRepository.findLanguageByNombreLenguaje("java"));
        assertNotNull(languageRepository.findLanguageByNombreLenguaje("python"));
        assertNotNull(languageRepository.findLanguageByNombreLenguaje("c"));
        assertNotNull(languageRepository.findLanguageByNombreLenguaje("cpp"));
        assertNotNull(languageRepository.findLanguageByNombreLenguaje("sql"));
    }

    // verificar el paso de mensajes por la cola -- rabbit
    @Test
    @DisplayName("Verificar metodo send de rabbit")
    @Disabled("Verificar metodo send de rabbit - Not implemented")
    void testSend() {

    }

    @Test
    @DisplayName("Verificar metodo receive de rabbit")
    @Disabled("Verificar metodo receive de rabbit - Not implemented")
    void testReceive() {

    }
}