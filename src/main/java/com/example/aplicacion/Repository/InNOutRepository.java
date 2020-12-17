package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InNOutRepository extends JpaRepository<InNOut, Long> {
    @Override
    Optional<InNOut> findById(Long aLong);
}
