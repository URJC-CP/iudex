package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Submission;
import es.urjc.etsii.grafo.iudex.entities.SubmissionProblemValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionProblemValidatorRepository extends JpaRepository<SubmissionProblemValidator, Long> {
    boolean existsSubmissionProblemValidatorById(Long id);

    boolean existsSubmissionProblemValidatorBySubmission(Submission submission);

    Optional<SubmissionProblemValidator> findSubmissionProblemValidatorById(Long aLong);

    Optional<SubmissionProblemValidator> findSubmissionProblemValidatorBySubmission(Submission submission);
}
