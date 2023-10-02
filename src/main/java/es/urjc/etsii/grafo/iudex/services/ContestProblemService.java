package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.ContestProblem;
import es.urjc.etsii.grafo.iudex.entities.Problem;
import es.urjc.etsii.grafo.iudex.repositories.ContestProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContestProblemService {

    @Autowired
    private ContestProblemRepository contestProblemRepository;

    public void save(ContestProblem contestProblem) {
        contestProblemRepository.save(contestProblem);
    }

    public Optional<ContestProblem> getContestProblemByContestAndProblem(Contest contest, Problem problem) {
        return contestProblemRepository.findByContestAndProblem(contest, problem);
    }

}
