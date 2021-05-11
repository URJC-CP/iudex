package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    public Optional<Language> getLanguage(Long id) {
        return languageRepository.findLanguageById(id);
    }

    public Optional<Language> getLanguageByName(String name) {
        return languageRepository.findLanguageByNombreLenguaje(name);
    }

    public List<Language> getNLanguages() {
        return languageRepository.findAll();
    }
}
