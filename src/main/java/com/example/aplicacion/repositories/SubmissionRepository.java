package com.example.aplicacion.repositories;

import com.example.aplicacion.entities.Contest;
import com.example.aplicacion.entities.Problem;
import com.example.aplicacion.entities.Result;
import com.example.aplicacion.entities.Submission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsSubmissionById(Long id);

    boolean existsSubmissionByResults(Result res);

    boolean existsSubmissionByProblemaAndContest(Problem problem, Contest contest);

    Optional<Submission> findSubmissionById(long id);

    Optional<Submission> findSubmissionByResults(Result res);

    List<Submission> findSubmissionsByProblemaAndContest(Problem problem, Contest contest);

    Page<Submission> findAll(Pageable pageable);

}
