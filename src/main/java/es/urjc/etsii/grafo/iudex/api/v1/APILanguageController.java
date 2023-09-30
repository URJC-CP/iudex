package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.Language;
import es.urjc.etsii.grafo.iudex.pojos.LanguageAPI;
import es.urjc.etsii.grafo.iudex.services.LanguageService;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/API/v1/")
@CrossOrigin(methods = {RequestMethod.GET})
public class APILanguageController {
    @Autowired
    private LanguageService languageService;

    @Operation( summary = "Return all languages")
    @GetMapping("language")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<List<LanguageAPI>> getLanguages() {
        List<Language> languageList = languageService.getAllLanguages();
        List<LanguageAPI> languageAPIS = new ArrayList<>();

        for (Language language : languageList) {
            languageAPIS.add(language.toLanguageAPI());
        }
        return new ResponseEntity<>(languageAPIS, HttpStatus.OK);
    }
}
