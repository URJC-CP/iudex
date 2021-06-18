package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    boolean existsResultById(Long aLong);

    Optional<Result> findResultById(long id);

    List<Result> findAll();
}
