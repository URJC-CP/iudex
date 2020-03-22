package com.example.aplicacion.rabbitMQ;

import com.example.aplicacion.Entities.Exercise;
import com.example.aplicacion.services.AnswerHandler;
import com.example.aplicacion.Entities.Answer;
import com.example.aplicacion.Repository.AnswerRepository;
import com.example.aplicacion.services.AnswerReviser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static java.lang.Thread.sleep;


@Service

public class ListenerDockerExecRabbit {
    private AnswerHandler ansHandler;
    private AnswerReviser ansReviser;

    @Autowired
    private AnswerRepository answerRepository;

    @PostConstruct
    public void init() {

        ansHandler = new AnswerHandler();
        ansReviser = new AnswerReviser();

    }




    public void handleMessage(Long mensaje){
        //Si hay procesadores disponibles

        //recibe el id del answer
        System.out.println(mensaje.toString() );
        Answer ans = answerRepository.findAnswerById(mensaje);
        Exercise ejer = new Exercise();
        ejer.setSalidaCorrecta("\t2\n" +
                "4\n" +
                "6\n" +
                "16");

        if(ans==null){
            throw new RuntimeException("EL ID:" +mensaje+ " DEL ANSWER NO ESTA EN LA BBDD");
        }
        else{
            ansHandler.ejecutorJava(ans);
            ansReviser.revisar(ans, ejer);

            //guardamos los resultados
            answerRepository.save(ans);

        }


    }

}
