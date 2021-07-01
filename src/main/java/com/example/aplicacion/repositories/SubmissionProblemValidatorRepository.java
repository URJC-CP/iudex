package com.example.aplicacion.repositories;

import com.example.aplicacion.entities.Submission;
import com.example.aplicacion.entities.SubmissionProblemValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionProblemValidatorRepository extends JpaRepository<SubmissionProblemValidator, Long> {
    boolean existsSubmissionProblemValidatorById(Long id);

    boolean existsSubmissionProblemValidatorBySubmission(Submission submission);

    Optional<SubmissionProblemValidator> findSubmissionProblemValidatorById(Long aLong);

    Optional<SubmissionProblemValidator> findSubmissionProblemValidatorBySubmission(Submission submission);
}
