package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    @Override
    Optional<Language> findById(Long aLong);
    Optional<Language> findLanguageByNombreLenguaje(String id);
    Optional<Language> findLanguageById(Long id);

    List<Language> findAll();
}
