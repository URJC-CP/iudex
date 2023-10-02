package es.urjc.etsii.grafo.iudex.events;

import es.urjc.etsii.grafo.iudex.events.publishers.IudexEventPublisher;
import es.urjc.etsii.grafo.iudex.events.publishers.IudexSimpleEventPublisher;
import es.urjc.etsii.grafo.iudex.events.types.IudexSimpleEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class IudexEventController {

    final IudexEventPublisher iudexEventPublisher;

    public IudexEventController(IudexSimpleEventPublisher iudexSimpleEventPublisher) {
        this.iudexEventPublisher = iudexSimpleEventPublisher;
    }

    // Mapped as /app/application
    @MessageMapping("/application")
    public void send(final IudexSimpleEvent event) throws Exception {
        iudexEventPublisher.sendMessage(event);
    }

}

