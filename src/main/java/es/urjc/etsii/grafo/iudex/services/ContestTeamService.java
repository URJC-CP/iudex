package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.ContestTeams;
import es.urjc.etsii.grafo.iudex.repositories.ContestTeamRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContestTeamService {

    @Autowired
    private ContestTeamRespository contestTeamRespository;

    public void save(ContestTeams contestTeams) {
        contestTeamRespository.save(contestTeams);
    }

}
