package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProblemService {
    @Autowired
    private ProblemRepository problemRepository;

    public List<Problem> getNProblemas(){
        return problemRepository.findAll();
    }
}
