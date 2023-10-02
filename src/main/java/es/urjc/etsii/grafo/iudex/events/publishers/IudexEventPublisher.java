package es.urjc.etsii.grafo.iudex.events.publishers;

import es.urjc.etsii.grafo.iudex.events.types.IudexEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public abstract class IudexEventPublisher {

    @Autowired
    protected final SimpMessagingTemplate simpMessagingTemplate;

    protected final String DESTINATION;

    protected IudexEventPublisher(SimpMessagingTemplate simpMessagingTemplate, String destination) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        DESTINATION = destination;
    }

    public void sendMessage(IudexEvent event, Object... args) {
        simpMessagingTemplate.convertAndSend(String.format(DESTINATION, args), event);
    }
}
