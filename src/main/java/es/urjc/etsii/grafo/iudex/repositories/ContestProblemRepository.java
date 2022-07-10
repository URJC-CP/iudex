package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.ContestProblem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestProblemRepository extends JpaRepository <ContestProblem, Long> {
    List<ContestProblem> findByContest(Contest contest);

    void deleteByContest(Contest contest);
}
