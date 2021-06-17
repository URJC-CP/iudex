package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Contest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Optional<Contest> findContestById(Long id);
    Optional<Contest> findContestByNombreContest(String name);

    Page<Contest> findAll(Pageable pageable);
}
