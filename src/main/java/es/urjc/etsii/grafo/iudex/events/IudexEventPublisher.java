package es.urjc.etsii.grafo.iudex.events;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class IudexEventPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public IudexEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendMessage(IudexEvent event) {
        simpMessagingTemplate.convertAndSend("/all/messages", event);
    }
}
