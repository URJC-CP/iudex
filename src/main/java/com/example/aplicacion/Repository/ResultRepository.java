  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Result;
  import org.springframework.data.jpa.repository.JpaRepository;

  import java.util.List;

  public interface ResultRepository extends JpaRepository<Result, Long> {


      Result findResultById(long id);
      List<Result> findAll();

  }
