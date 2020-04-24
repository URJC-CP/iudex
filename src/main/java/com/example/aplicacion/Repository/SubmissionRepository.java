  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Submission;
  import org.springframework.data.domain.PageRequest;
  import org.springframework.data.domain.Pageable;
  import org.springframework.data.jpa.repository.JpaRepository;

  import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

	Submission findAnswerById(long id);

}
