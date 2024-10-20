package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.Language;
import es.urjc.etsii.grafo.iudex.repositories.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageService {
    private final LanguageRepository languageRepository;

    public LanguageService(LanguageRepository languageRepository) {
        this.languageRepository = languageRepository;
    }

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

    public Language saveLanguage(Language language) {
        return languageRepository.save(language);
    }

    public List<Language> getNLanguages() {
        return languageRepository.findAll();
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }
}
