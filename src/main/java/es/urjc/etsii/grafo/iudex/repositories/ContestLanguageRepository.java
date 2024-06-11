package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Contest;
import es.urjc.etsii.grafo.iudex.entities.ContestLanguages;
import es.urjc.etsii.grafo.iudex.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestLanguageRepository extends JpaRepository <ContestLanguages, Long> {

    boolean existsByContestAndLenguajes(Contest contest, Language lenguajes);

    ContestLanguages findByContestAndLenguajes(Contest contest, Language lenguajes);

}
