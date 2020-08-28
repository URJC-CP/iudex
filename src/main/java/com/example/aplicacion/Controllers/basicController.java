package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.InNOut;
import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.InNOutRepository;
import com.example.aplicacion.Repository.LanguageRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.SubmissionRepository;
import com.example.aplicacion.services.ResultHandler;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.persistence.CascadeType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Controller
public class basicController {
    @Autowired
    public SubmissionRepository submissionRepository;
    @Autowired
    public ProblemRepository problemRepository;
    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private ResultHandler resultHandler;
    @Autowired
    private InNOutRepository inNOutRepository;
    @PostConstruct
    public void init() {

        //Creamos el lenguaje JAVA
        File dckfl = new File("DOCKERS/Java/Dockerfile");
        String imageId = resultHandler.buildImage(dckfl);
        Language lenguaje = new Language("java", imageId);
        languageRepository.save(lenguaje);

        //Creamos el lenguaje Python
        File dckfl2 = new File("DOCKERS/Python3/Dockerfile");
        String imageId2 = resultHandler.buildImage(dckfl2);
        Language lenguaje2 = new Language("python3", imageId2);
        languageRepository.save(lenguaje2);


        Problem problem = new Problem();
        problem.setNombreEjercicio("Problema de prueba");


        List<InNOut> entradas = new ArrayList<>();
        InNOut aux1 = new InNOut("1","1\n" +"2\n" +"3\n" +"8");
        inNOutRepository.save(aux1);
        entradas.add(aux1);
        InNOut aux2 = new InNOut("2","5\n" +"6\n" +"7\n" +"8");
        inNOutRepository.save(aux2);
        entradas.add(aux2);
        problem.setEntradaOculta(entradas);

        List<InNOut> salidas = new ArrayList<>();
        InNOut aux3 = new InNOut("1", "\t2\n" +  "4\n" + "6\n" + "16");
        inNOutRepository.save(aux3);
        salidas.add(aux3);
        InNOut aux4 = new InNOut("2","\t10\n" +  "12\n" + "14\n" +"16");
        inNOutRepository.save(aux4);
        salidas.add(aux4);


        problem.setSalidaOculta(salidas);

        problemRepository.save(problem);

    }
}
