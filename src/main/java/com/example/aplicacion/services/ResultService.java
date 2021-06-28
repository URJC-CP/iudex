package com.example.aplicacion.services;

import com.example.aplicacion.entities.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService extends BaseService {
    public Result getResult(String resultId) {
        return resultRepository.findResultById(Long.parseLong(resultId)).orElseThrow();
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }
}