package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Team;
import es.urjc.etsii.grafo.iudex.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsTeamById(Long id);

    boolean existsTeamByNombreEquipo(String name);

    boolean existsTeamByParticipantesContains(User user);

    Optional<Team> findByNombreEquipo(String name);

    Optional<Team> findTeamById(Long l);
}

