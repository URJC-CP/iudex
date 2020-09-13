package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByNombreEquipo(String name);

    boolean existsTeamByParticipantesContains(User user);

    Team findByNombreEquipo(String name);
    Team findTeamById(Long l);

}

