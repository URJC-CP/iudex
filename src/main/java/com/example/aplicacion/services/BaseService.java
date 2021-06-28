package com.example.aplicacion.services;

import com.example.aplicacion.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {
    @Autowired
    protected ContestRepository contestRepository;
    @Autowired
    protected ContestService contestService;
    @Autowired
    protected LanguageRepository languageRepository;
    @Autowired
    protected LanguageService languageService;

    @Autowired
    protected ProblemRepository problemRepository;
    @Autowired
    protected ProblemService problemService;
    @Autowired
    protected SampleRepository sampleRepository;
    @Autowired
    protected ProblemValidatorService problemValidatorService;

    @Autowired
    protected TeamRepository teamRepository;
    @Autowired
    protected TeamService teamService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected UserService userService;

    @Autowired
    protected SubmissionRepository submissionRepository;
    @Autowired
    protected SubmissionService submissionService;
    @Autowired
    protected ResultRepository resultRepository;
    @Autowired
    protected ResultService resultService;
}
