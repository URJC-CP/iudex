package es.urjc.etsii.grafo.iudex.events.publishers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class IudexSpecificSubmissionEventPublisher extends IudexEventPublisher {

    public IudexSpecificSubmissionEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        super(simpMessagingTemplate, "/specific/submissions/%s");
    }
}
