package com.example.aplicacion.services;

import com.example.aplicacion.Entities.LanguageAPI;
import com.example.aplicacion.Repository.LanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;

    public List<LanguageAPI> getNLanguages(){
        return languageRepository.findAll();
    }
}
