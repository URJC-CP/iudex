package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    boolean existsProblemById(Long aLong);
    boolean existsProblemByNombreEjercicio(String nombre);

    Optional<Problem> findProblemById(long id);
    Optional<Problem> findProblemByNombreEjercicio(String nombreEjercicio);

    Page<Problem> findAll(Pageable pageable);
}
