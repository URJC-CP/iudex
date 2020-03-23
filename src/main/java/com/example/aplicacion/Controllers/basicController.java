package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Exercise;
import com.example.aplicacion.Repository.AnswerRepository;
import com.example.aplicacion.Repository.ExerciseRepository;
import com.example.aplicacion.services.AnswerHandler;
import com.example.aplicacion.services.AnswerReviser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;


@Controller
public class basicController {
    @Autowired
    public AnswerRepository answerRepository;
    @Autowired
    public ExerciseRepository exerciseRepository;

    @PostConstruct
    public void init() {

        Exercise ejer = new Exercise();
        ejer.setSalidaCorrecta("\t2\n" +  "4\n" + "6\n" +    "16");
        ejer.setNombreEjercicio("prueba");
        exerciseRepository.save(ejer);

    }
}
