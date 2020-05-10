package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.SubmissionProblemValidator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionProblemValidatorRepository  extends JpaRepository<SubmissionProblemValidator, Long> {
    SubmissionProblemValidator findById(long id);

    SubmissionProblemValidator findSubmissionProblemValidatorBySubmission(Submission submission);
}
