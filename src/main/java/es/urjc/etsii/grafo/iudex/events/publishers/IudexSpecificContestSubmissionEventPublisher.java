package es.urjc.etsii.grafo.iudex.events.publishers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class IudexSpecificContestSubmissionEventPublisher extends IudexEventPublisher {

    public IudexSpecificContestSubmissionEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        super(simpMessagingTemplate, "/specific/contests/%s/submissions");
    }
}
