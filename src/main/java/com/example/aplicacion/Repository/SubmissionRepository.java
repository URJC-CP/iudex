  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Contest;
  import com.example.aplicacion.Entities.Problem;
  import com.example.aplicacion.Entities.Result;
  import com.example.aplicacion.Entities.Submission;
  import org.springframework.data.domain.Page;
  import org.springframework.data.domain.Pageable;
  import org.springframework.data.jpa.repository.JpaRepository;

  import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

	Submission findSubmissionById(long id);

	Submission findSubmissionByResults(Result res);
	List<Submission> findSubmissionsByProblemaAndContest(Problem problem, Contest contest);
	Page<Submission> findAll(Pageable pageable);


}
