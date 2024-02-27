package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Team;
import es.urjc.etsii.grafo.iudex.entities.TeamUser;
import es.urjc.etsii.grafo.iudex.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTeamRespository extends JpaRepository<TeamUser, Long> {

    boolean existsByTeamAndUser(Team team, User user);

    List<TeamUser> findAllByUser(User user);

}
