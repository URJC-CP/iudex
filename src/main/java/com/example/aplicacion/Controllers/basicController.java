package com.example.aplicacion.Controllers;

import com.example.aplicacion.services.AnswerHandler;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;


@Controller
public class basicController {

    public AnswerHandler ansHandler;
    @PostConstruct
    public void init() {

        ansHandler = new AnswerHandler();

    }
}
