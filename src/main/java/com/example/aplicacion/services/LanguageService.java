package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    public List<Language> getNLanguages(){
        return languageRepository.findAll();
    }
}
