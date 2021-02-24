package com.example.aplicacion.rabbitMQ;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Repository.ResultRepository;
import com.example.aplicacion.services.ResultHandler;
import com.example.aplicacion.Repository.SubmissionRepository;
//import com.example.aplicacion.services.AnswerReviser;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.io.IOException;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

//Clase que se encarga de recibir un result y mandarlo al servicio docker.
@Service
public class RabbitResultExecutionReceiver {
    @Autowired
    private ResultHandler resultHandler;
    @Autowired
    private  RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitResultReviserSender rabbitResultReviserSender;
    


    //LIstener que recibe el objeto resultado desde la cola
    @RabbitListener(queues = ConfigureRabbitMq.QUEUE_NAME)
    public void handleMessage2(Result res){

        //Primero ejecutamos el codigo y guardamos
        try {
            resultHandler.ejecutor(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Enviamos el res a la cola de reviser
        rabbitResultReviserSender.sendMenssage(res);
    }


/*
    //No esta funcionando ahora mismo
    public void handleMessage(Result res){
        //Si hay procesadores disponibles


        //recibe el id del answer
        //System.out.println(res.toString() );
        //Submission ans = submissionRepository.findAnswerById(mensaje);

        if(res==null){
            throw new RuntimeException("EL ID:" +res.getId()+ " DEL ANSWER NO ESTA EN LA BBDD");
        }
        else{
            ansHandler.ejecutorJava(res);
            //ansReviser.revisar(res);

            //guardamos los resultados
            resultRepository.save(res);

        }


    }

    */
}
