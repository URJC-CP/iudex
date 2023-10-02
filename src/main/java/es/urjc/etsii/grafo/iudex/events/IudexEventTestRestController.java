package es.urjc.etsii.grafo.iudex.events;

import es.urjc.etsii.grafo.iudex.events.publishers.IudexSimpleEventPublisher;
import es.urjc.etsii.grafo.iudex.events.publishers.IudexSpecificContestSubmissionEventPublisher;
import es.urjc.etsii.grafo.iudex.events.publishers.IudexSpecificContestTeamSubmissionEventPublisher;
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

    final IudexSpecificContestSubmissionEventPublisher iudexSpecificContestSubmissionEventPublisher;

    final IudexSpecificContestTeamSubmissionEventPublisher iudexSpecificContestTeamSubmissionEventPublisher;

    public IudexEventTestRestController(IudexSimpleEventPublisher iudexSimpleEventPublisher,
                                        IudexSubmissionEventPublisher iudexSubmissionEventPublisher,
                                        IudexSpecificContestSubmissionEventPublisher iudexSpecificContestSubmissionEventPublisher,
                                        IudexSpecificContestTeamSubmissionEventPublisher iudexSpecificContestTeamSubmissionEventPublisher) {

        this.iudexSimpleEventPublisher = iudexSimpleEventPublisher;
        this.iudexSubmissionEventPublisher = iudexSubmissionEventPublisher;
        this.iudexSpecificContestSubmissionEventPublisher = iudexSpecificContestSubmissionEventPublisher;
        this.iudexSpecificContestTeamSubmissionEventPublisher = iudexSpecificContestTeamSubmissionEventPublisher;
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

    @PostMapping("/testEvent/contests/{contest_id}/submissions")
    public void sendSubmission(@RequestBody IudexSubmissionEvent event, @PathVariable String contest_id) {
        iudexSpecificContestSubmissionEventPublisher.sendMessage(event, contest_id);
    }

    @PostMapping("/testEvent/contests/{contest_id}/teams/{team_id}/submissions")
    public void sendSubmission(@RequestBody IudexSubmissionEvent event, @PathVariable String contest_id, @PathVariable String team_id) {
        iudexSpecificContestTeamSubmissionEventPublisher.sendMessage(event, contest_id, team_id);
    }

}
