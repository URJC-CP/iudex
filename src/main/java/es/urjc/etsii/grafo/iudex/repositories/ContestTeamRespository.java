package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.ContestTeams;
import es.urjc.etsii.grafo.iudex.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContestTeamRespository extends JpaRepository<ContestTeams, Long> {

    List<ContestTeams> findByContest(Contest contest);

    Optional<ContestTeams> findByContestAndTeams(Contest contest, Team team);

    void deleteByContest(Contest contest);
}
