package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.*;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTeamRespository extends JpaRepository<TeamUser, Long> {

    boolean existsByTeamAndUser(Team team, User user);

}
