package com.example.aplicacion.Controllers;


import com.example.aplicacion.Entities.Answer;
import com.example.aplicacion.Entities.Exercise;
import com.example.aplicacion.Repository.AnswerRepository;
import com.example.aplicacion.Repository.ExerciseRepository;
import com.example.aplicacion.rabbitMQ.ConfigureRabbitMq;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

import java.util.ArrayList;


@Controller
public class IndiceController {

    private  final RabbitTemplate rabbitTemplate;

    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    public ExerciseRepository exerciseRepository;

    //Inicio del rabbittemplate
    public IndiceController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        //this.rabbitTemplate = new RabbitTemplate();

    }
    @GetMapping("/")
    public String index(Model model){
        //Pruebas de rabbit
        List<Exercise> listaEjercicios = exerciseRepository.findAll();
        model.addAttribute("exercices", listaEjercicios);


        return "index";
    }

    @PostMapping("/answerSubida")
    public String subida(Model model, @RequestParam MultipartFile codigo, @RequestParam MultipartFile entrada, @RequestParam String exerciseAsig) throws IOException {

        String cod = new String(codigo.getBytes());
        String ent = new String(entrada.getBytes());
        String lenguaje = "java";

        Answer ans = new Answer(cod, ent, lenguaje);    //Creamos la entrada
        ans.setEjercicio(exerciseRepository.findExerciseByNombreEjercicio(exerciseAsig));
        answerRepository.save(ans);                     //La guardamos en la bbdd

        //ansHandler.ejecutorJava(ans);

        //Paso de mensaje a la cola
        rabbitTemplate.convertAndSend(ConfigureRabbitMq.EXCHANGE_NAME, "docker.springmesage", ans.getId());


        //Cargamos todos los ejercicios disponibles


        return "answerSubida";
    }
    @GetMapping("/scoreboard")
    public String subida (Model model){

        //Cargamos la BBDD de answer en el scoreboard
        List<Answer> listAns = answerRepository.findAll();
        model.addAttribute("answers", listAns);


        return "scoreboard";
    }
}
