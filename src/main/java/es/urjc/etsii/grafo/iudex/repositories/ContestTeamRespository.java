package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.ContestProblem;
import es.urjc.etsii.grafo.iudex.entities.ContestTeams;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContestTeamRespository extends JpaRepository<ContestTeams, Long> {

    List<ContestTeams> findByContest(Contest contest);

    void deleteByContest(Contest contest);
}
