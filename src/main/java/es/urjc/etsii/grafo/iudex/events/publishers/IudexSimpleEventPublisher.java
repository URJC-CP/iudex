package es.urjc.etsii.grafo.iudex.events.publishers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class IudexSimpleEventPublisher extends IudexEventPublisher {

    public IudexSimpleEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        super(simpMessagingTemplate, "/all/simple");
    }
}
