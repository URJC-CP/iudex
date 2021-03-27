package com.example.aplicacion;

import com.example.aplicacion.services.ResultHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TheJudgeApplicationOrchestator {

    public static void main(String[] args) {

        SpringApplication.run(TheJudgeApplicationOrchestator.class, args);
        //new DockerHelloWorld();
        new ResultHandler();
    }

}
