package es.urjc.etsii.grafo.iudex.rabbitmq;

import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.entities.Submission;
import es.urjc.etsii.grafo.iudex.repositories.ResultRepository;
import es.urjc.etsii.grafo.iudex.repositories.SubmissionRepository;
import es.urjc.etsii.grafo.iudex.services.ResultReviser;
import es.urjc.etsii.grafo.iudex.services.SubmissionReviserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//Clase que se encarga de recibir el result
@Service
public class RabbitResultRevieserReceiver {
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private SubmissionReviserService submissionReviserService;


    @RabbitListener(queues = ConfigureRabbitMq.QUEUE_NAME2)
    @Transactional
    public void handleMessage(Result res) {
        //enviamos el res a revisar
        new ResultReviser().revisar(res);

        //lo guardamos
        resultRepository.save(res);

        Optional<Submission> submissionOptional = submissionRepository.findSubmissionByResults(res);
        Submission submission = submissionOptional.orElseThrow();
        submission.sumarResultCorregido();

        //en caso de que ya se hayan corregido todos mandaremos una senal para que se valide el submission
        if (submission.isTerminadoDeEjecutarResults()) {
            submissionReviserService.revisarSubmission(submission);
        }
        submissionRepository.save(submission);

    }
}
