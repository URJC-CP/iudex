  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Problem;
  import org.springframework.data.domain.Page;
  import org.springframework.data.domain.Pageable;
  import org.springframework.data.jpa.repository.JpaRepository;

  public interface ProblemRepository extends JpaRepository<Problem, Long> {


      Problem findProblemById(long id);
      Problem findProblemByNombreEjercicio(String nombreEjercicio);
      Problem findById(long id);
      boolean existsByNombreEjercicio(String nombre);


      Page<Problem> findAll(Pageable pageable);


  }
