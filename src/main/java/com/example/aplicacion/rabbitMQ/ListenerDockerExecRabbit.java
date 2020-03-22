package com.example.aplicacion.rabbitMQ;

import com.example.aplicacion.AnswerHandler;
import com.example.aplicacion.Entities.Answer;
import com.example.aplicacion.Repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import static java.lang.Thread.sleep;


@Service

public class ListenerDockerExecRabbit {
    private AnswerHandler ansHandler;


    @Autowired
    private AnswerRepository answerRepository;

    @PostConstruct
    public void init() {

        ansHandler = new AnswerHandler();

    }




    public void handleMessage(Long mensaje){
        //Si hay procesadores disponibles

        //recibe el id del answer
        System.out.println(mensaje.toString() );
        Answer ans = answerRepository.findAnswerById(mensaje);

        if(ans==null){
            throw new RuntimeException("EL ID DEL ANSWER NO ESTA EN LA BBDD");
        }
        else{
            ansHandler.ejecutorJava(ans);
            answerRepository.save(ans);

        }


    }

}
