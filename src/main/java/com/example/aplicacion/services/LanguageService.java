package com.example.aplicacion.services;

import com.example.aplicacion.entities.Language;
import com.example.aplicacion.repositories.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    public Optional<Language> getLanguage(String id) {
        return languageRepository.findLanguageById(Long.parseLong(id));
    }

    public Optional<Language> getLanguageByName(String name) {
        if (name.equals("python")) {
            name += "3";
        }
        return languageRepository.findLanguageByNombreLenguaje(name);
    }

    public boolean existsLanguageByName(String name) {
        return languageRepository.existsLanguageByNombreLenguaje(name);
    }

    public void saveLanguage(Language language) {
        languageRepository.save(language);
    }

    public List<Language> getNLanguages() {
        return languageRepository.findAll();
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }
}
