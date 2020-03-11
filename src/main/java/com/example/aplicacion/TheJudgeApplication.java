package com.example.aplicacion;

import Docker.DockerHelloWorld;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TheJudgeApplication {

    public static void main(String[] args) {

        SpringApplication.run(TheJudgeApplication.class, args);
        //new DockerHelloWorld();
        new AnswerHandler();
    }

}
