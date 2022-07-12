package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.Problem;
import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.entities.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsSubmissionById(Long id);

    boolean existsSubmissionByResults(Result res);

    boolean existsSubmissionByProblemAndContest(Problem problem, Contest contest);

    Optional<Submission> findSubmissionById(long id);

    Optional<Submission> findSubmissionByResults(Result res);

    List<Submission> findSubmissionsByProblemAndContest(Problem problem, Contest contest);

    Page<Submission> findAll(Pageable pageable);

}
