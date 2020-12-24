package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.SubmissionProblemValidator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionProblemValidatorRepository extends JpaRepository<SubmissionProblemValidator, Long> {


    Optional<SubmissionProblemValidator> findSubmissionProblemValidatorBySubmission(Submission submission);
}
