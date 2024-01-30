package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.repositories.ResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResultService {
    private final ResultRepository resultRepository;

    public ResultService(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    public Result getResult(String resultId) {
        return resultRepository.findResultById(Long.parseLong(resultId)).orElseThrow();
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }
}