package es.urjc.etsii.grafo.iudex.smoketest;

import es.urjc.etsii.grafo.iudex.api.v1.APIContestController;
import es.urjc.etsii.grafo.iudex.api.v1.APIProblemController;
import es.urjc.etsii.grafo.iudex.api.v1.APISubmissionController;
import es.urjc.etsii.grafo.iudex.api.v1.APITeamController;
import es.urjc.etsii.grafo.iudex.rabbitmq.RabbitResultExecutionSender;
import es.urjc.etsii.grafo.iudex.rabbitmq.RabbitResultRevieserReceiver;
import es.urjc.etsii.grafo.iudex.rabbitmq.RabbitResultReviserSender;
import es.urjc.etsii.grafo.iudex.repositories.*;
import es.urjc.etsii.grafo.iudex.services.OnStartRunner;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        fail("Not implemented");
    }

    @Test
    @DisplayName("Verificar metodo receive de rabbit")
    @Disabled("Verificar metodo receive de rabbit - Not implemented")
    void testReceive() {
        fail("Not implemented");
    }
}