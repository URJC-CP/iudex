package es.urjc.etsii.grafo.iudex.service;

import es.urjc.etsii.grafo.iudex.entity.Result;
import es.urjc.etsii.grafo.iudex.repository.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

    public Result getResult(String resultId) {
        return resultRepository.findResultById(Long.parseLong(resultId)).orElseThrow();
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }
}