  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Answer;
  import org.springframework.data.jpa.repository.JpaRepository;
  import org.springframework.data.jpa.repository.Query;

  import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {


	List<Answer> findById(long id);

}
