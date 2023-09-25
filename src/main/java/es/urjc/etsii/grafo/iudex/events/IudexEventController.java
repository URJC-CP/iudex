package es.urjc.etsii.grafo.iudex.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class IudexEventController {

    @Autowired
    IudexEventPublisher iudexEventPublisher;

    // Mapped as /app/application
    @MessageMapping("/application")
    public void send(final IudexEvent event) throws Exception {
        iudexEventPublisher.sendMessage(event);
    }

}