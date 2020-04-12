  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Problem;
  import org.springframework.data.jpa.repository.JpaRepository;
  import org.springframework.stereotype.Repository;

  import java.util.List;

  public interface ProblemRepository extends JpaRepository<Problem, Long> {


      Problem findProblemByNombreEjercicio(String nombreEjercicio);
      List<Problem> findAll();

  }
