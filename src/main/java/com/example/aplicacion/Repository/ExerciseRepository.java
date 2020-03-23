  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Answer;
  import com.example.aplicacion.Entities.Exercise;
  import org.springframework.data.jpa.repository.JpaRepository;

  import java.util.List;

  public interface ExerciseRepository extends JpaRepository<Exercise, Long> {


      Exercise findExerciseByNombreEjercicio(String nombreEjercicio);
      List<Exercise> findAll();

  }
