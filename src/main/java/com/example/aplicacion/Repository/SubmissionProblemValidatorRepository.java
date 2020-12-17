package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.SubmissionProblemValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionProblemValidatorRepository extends JpaRepository<SubmissionProblemValidator, Long> {
    @Override
    Optional<SubmissionProblemValidator> findById(Long id);
    Optional<SubmissionProblemValidator> findSubmissionProblemValidatorBySubmission(Submission submission);
}
