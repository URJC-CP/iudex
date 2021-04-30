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
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import com.example.aplicacion.rabbitMQ.RabbitResultRevieserReceiver;
import com.example.aplicacion.rabbitMQ.RabbitResultReviserSender;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("verificar que la app este desplegada")
    public void contextLoads() {
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
        assertNotNull(basicController);
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
    }

    // verificar estado inicial de los repositorios
    @Test
    @DisplayName("Verificar estado inicial de los repositorios")
    public void emptyRepositories() {
        assertEquals(contestRepository.findAll().size(), 1);
        assertTrue(sampleRepository.findAll().isEmpty());
        assertFalse(languageRepository.findAll().isEmpty());
        assertTrue(problemRepository.findAll().isEmpty());
        assertTrue(resultRepository.findAll().isEmpty());
        assertTrue(submissionRepository.findAll().isEmpty());
        assertEquals(teamRepository.findAll().size(), 1);
        assertEquals(userRepository.findAll().size(), 1);
    }

    // verificar los lenguajes
    @Test
    @DisplayName("Verificar los lenguajes soportados")
    public void languageTest() {
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
    public void testSend() {

    }

    @Test
    @DisplayName("Verificar metodo receive de rabbit")
    @Disabled("Verificar metodo receive de rabbit - Not implemented")
    public void testReceive() {

    }
}