package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.Language;
import es.urjc.etsii.grafo.iudex.pojos.LanguageAPI;
import es.urjc.etsii.grafo.iudex.services.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/API/v1/")
public class APILanguageController {
    private final LanguageService languageService;

    public APILanguageController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @Operation( summary = "Return all languages")
    @GetMapping("language")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<LanguageAPI>> getLanguages() {
        List<Language> languageList = languageService.getAllLanguages();
        List<LanguageAPI> languageAPIS = new ArrayList<>();

        for (Language language : languageList) {
            languageAPIS.add(language.toLanguageAPI());
        }
        return new ResponseEntity<>(languageAPIS, HttpStatus.OK);
    }
}
