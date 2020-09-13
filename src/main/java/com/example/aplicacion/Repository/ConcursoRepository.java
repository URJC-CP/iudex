package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Concurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConcursoRepository extends JpaRepository<Concurso, Long> {
    boolean existsByNombreConcurso(String nombre);

    Concurso findConcursoById(Long id);
}
