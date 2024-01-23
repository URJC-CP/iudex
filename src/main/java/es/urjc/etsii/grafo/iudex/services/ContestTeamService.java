package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.ContestTeams;
import es.urjc.etsii.grafo.iudex.repositories.ContestTeamRespository;
import org.springframework.stereotype.Service;

@Service
public class ContestTeamService {

    private final ContestTeamRespository contestTeamRespository;

    public ContestTeamService(ContestTeamRespository contestTeamRespository) {
        this.contestTeamRespository = contestTeamRespository;
    }

    public void save(ContestTeams contestTeams) {
        contestTeamRespository.save(contestTeams);
    }

}
