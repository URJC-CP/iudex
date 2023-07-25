package es.urjc.etsii.grafo.iudex.integrationtest.api_controllers;

import es.urjc.etsii.grafo.iudex.api.v1.APILanguageController;
import es.urjc.etsii.grafo.iudex.entities.Language;
import es.urjc.etsii.grafo.iudex.pojos.LanguageAPI;
import es.urjc.etsii.grafo.iudex.services.LanguageService;
import es.urjc.etsii.grafo.iudex.utils.JSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestAPILanguageController {

    @Autowired
    private WebApplicationContext context;

    private final JSONConverter jsonConverter = new JSONConverter();

    private final String baseURL = "/API/v1/language";

    private MockMvc mockMvc;

    @MockBean
    private LanguageService languageService;

    @BeforeEach
    public void init() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

    }

    @Test
    @DisplayName("Return all languages")
    void getLanguages() throws Exception {
        List<Language> languages = new ArrayList<>();

        Language language = new Language();
        language.setId(1L);
        language.setNombreLenguaje("python3");
        languages.add(language);
        when(languageService.getAllLanguages()).thenReturn(languages);

        List<LanguageAPI> languageAPIList = new ArrayList<>();
        languageAPIList.add(language.toLanguageAPI());

        String result = mockMvc.perform(get(baseURL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(jsonConverter.convertObjectToJSON(languageAPIList), result);

        language = new Language();
        language.setId(2L);
        language.setNombreLenguaje("java");
        languages.add(language);
        when(languageService.getAllLanguages()).thenReturn(languages);
        languageAPIList.add(language.toLanguageAPI());

        result = mockMvc.perform(get(baseURL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(jsonConverter.convertObjectToJSON(languageAPIList), result);

        language = new Language();
        language.setId(3L);
        language.setNombreLenguaje("cpp");
        languages.add(language);
        when(languageService.getAllLanguages()).thenReturn(languages);
        languageAPIList.add(language.toLanguageAPI());

        result = mockMvc.perform(get(baseURL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(jsonConverter.convertObjectToJSON(languageAPIList), result);

        language = new Language();
        language.setId(4L);
        language.setNombreLenguaje("c");
        languages.add(language);
        when(languageService.getAllLanguages()).thenReturn(languages);
        languageAPIList.add(language.toLanguageAPI());

        result = mockMvc.perform(get(baseURL).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andDo(print()).andReturn().getResponse().getContentAsString();
        assertEquals(jsonConverter.convertObjectToJSON(languageAPIList), result);
    }
}