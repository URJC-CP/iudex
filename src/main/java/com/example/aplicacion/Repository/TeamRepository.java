package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Team;
import com.example.aplicacion.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByNombreEquipo(String name);
    boolean existsTeamByParticipantesContains(User user);

    @Override
    Optional<Team> findById(Long aLong);
    Optional<Team> findByNombreEquipo(String name);
    Optional<Team> findTeamById(Long l);
}

