package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    boolean existsByNombreEjercicio(String nombre);

    Optional<Problem> findProblemById(long id);
    Optional<Problem> findProblemByNombreEjercicio(String nombreEjercicio);
    Optional<Problem> findById(long id);

    Page<Problem> findAll(Pageable pageable);
}
