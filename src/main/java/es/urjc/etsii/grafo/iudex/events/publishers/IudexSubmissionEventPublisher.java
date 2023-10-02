package es.urjc.etsii.grafo.iudex.events.publishers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class IudexSubmissionEventPublisher extends IudexEventPublisher {

    public IudexSubmissionEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        super(simpMessagingTemplate, "/all/submissions");
    }
}
