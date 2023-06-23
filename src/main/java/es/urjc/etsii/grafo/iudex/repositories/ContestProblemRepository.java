package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.ContestProblem;
import es.urjc.etsii.grafo.iudex.entities.Problem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestProblemRepository extends JpaRepository <ContestProblem, Long> {
    List<ContestProblem> findByContest(Contest contest);

    void deleteByContest(Contest contest);

    Optional<ContestProblem> findByContestAndProblem(Contest contest, Problem problem);
}
