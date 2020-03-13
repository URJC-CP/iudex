package com.example.aplicacion.Controllers;


import com.example.aplicacion.AnswerHandler;
import com.example.aplicacion.Entities.Answer;
import com.example.aplicacion.Repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;


@Controller
public class IndiceController {

    private AnswerHandler ansHandler;

    @Autowired
    private AnswerRepository answerRepository;

    @PostConstruct
    public void init() {

        ansHandler = new AnswerHandler();

    }
    @GetMapping("/")
    public String index(Model model){
        System.out.println("HEY");

        return "index";
    }

    @PostMapping("/subida")
    public String subida(Model model, @RequestParam MultipartFile codigo, @RequestParam MultipartFile entrada) throws IOException {

        String cod = new String(codigo.getBytes());
        String ent = new String(entrada.getBytes());

        Answer ans = new Answer(cod, ent);    //Creamos la entrada
        answerRepository.save(ans);                     //La guardamos en la bbdd

        ansHandler.ejecutorJava(ans);


        return "salida";
    }
}
