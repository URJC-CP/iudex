package es.urjc.etsii.grafo.iudex.events;

import es.urjc.etsii.grafo.iudex.events.publishers.IudexSimpleEventPublisher;
import es.urjc.etsii.grafo.iudex.events.publishers.IudexSpecificSubmissionEventPublisher;
import es.urjc.etsii.grafo.iudex.events.publishers.IudexSubmissionEventPublisher;
import es.urjc.etsii.grafo.iudex.events.types.IudexSimpleEvent;
import es.urjc.etsii.grafo.iudex.events.types.IudexSubmissionEvent;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IudexEventTestRestController {

    final IudexSimpleEventPublisher iudexSimpleEventPublisher;

    final IudexSubmissionEventPublisher iudexSubmissionEventPublisher;

    final IudexSpecificSubmissionEventPublisher iudexSpecificSubmissionEventPublisher;

    public IudexEventTestRestController(IudexSimpleEventPublisher iudexSimpleEventPublisher,
                                        IudexSubmissionEventPublisher iudexSubmissionEventPublisher,
                                        IudexSpecificSubmissionEventPublisher iudexSpecificSubmissionEventPublisher) {

        this.iudexSimpleEventPublisher = iudexSimpleEventPublisher;
        this.iudexSubmissionEventPublisher = iudexSubmissionEventPublisher;
        this.iudexSpecificSubmissionEventPublisher = iudexSpecificSubmissionEventPublisher;
    }

    @PostMapping("/testEvent")
    public void send() {
        iudexSimpleEventPublisher.sendMessage(new IudexSimpleEvent("Test Event"));
    }

    @PostMapping("/testEvent/simple")
    public void sendSimple(@RequestBody IudexSimpleEvent event) {
        iudexSimpleEventPublisher.sendMessage(event);
    }

    @PostMapping("/testEvent/submissions")
    public void sendSubmission(@RequestBody IudexSubmissionEvent event) {
        iudexSubmissionEventPublisher.sendMessage(event);
    }

    @PostMapping("/testEvent/submissions/{id}")
    public void sendSubmission(@RequestBody IudexSubmissionEvent event, @PathVariable String id) {
        iudexSpecificSubmissionEventPublisher.sendMessage(event, id);
    }

}
