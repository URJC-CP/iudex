package es.urjc.etsii.grafo.iudex.controller.api;

import es.urjc.etsii.grafo.iudex.entity.Language;
import es.urjc.etsii.grafo.iudex.pojo.LanguageAPI;
import es.urjc.etsii.grafo.iudex.service.LanguageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/API/v1/")
@CrossOrigin(methods = {RequestMethod.GET})
public class APILanguageController {
    @Autowired
    private LanguageService languageService;

    @ApiOperation("Return all languages")
    @GetMapping("language")
    public ResponseEntity<List<LanguageAPI>> getLanguages() {
        List<Language> languageList = languageService.getAllLanguages();
        List<LanguageAPI> languageAPIS = new ArrayList<>();

        for (Language language : languageList) {
            languageAPIS.add(language.toLanguageAPI());
        }
        return new ResponseEntity<>(languageAPIS, HttpStatus.OK);
    }
}
