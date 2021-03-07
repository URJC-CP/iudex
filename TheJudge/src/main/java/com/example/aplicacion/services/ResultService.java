package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {
    @Autowired
    ResultRepository resultRepository;


    public Result getResult(String resultId){
        return resultRepository.findResultById(Long.valueOf(resultId));

    }
    public List<Result> getAllResults(){
        return resultRepository.findAll();
    }
}
