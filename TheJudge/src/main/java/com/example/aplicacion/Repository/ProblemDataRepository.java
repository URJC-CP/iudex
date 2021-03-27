package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.ProblemData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemDataRepository extends JpaRepository<ProblemData, Long> {

}
