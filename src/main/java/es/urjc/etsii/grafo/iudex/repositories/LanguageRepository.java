package es.urjc.etsii.grafo.iudex.repositories;

import es.urjc.etsii.grafo.iudex.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    boolean existsLanguageById(Long aLong);

    boolean existsLanguageByNombreLenguaje(String name);

    Optional<Language> findLanguageByNombreLenguaje(String id);

    Optional<Language> findLanguageById(Long id);

    List<Language> findAll();
}
