package com.example.aplicacion.rabbitMQ;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Repository.ResultRepository;
import com.example.aplicacion.services.ResultHandler;
import com.example.aplicacion.Repository.SubmissionRepository;
//import com.example.aplicacion.services.AnswerReviser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;

//Clase que se encarga de recibir un result y mandarlo al servicio docker.
@Service
public class RabbitResultExecutionReceiver {
    private ResultHandler ansHandler;
    private final RabbitTemplate rabbitTemplate;


    public RabbitResultExecutionReceiver(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @Autowired
    private ResultRepository resultRepository;

    @PostConstruct
    public void init() {

        ansHandler = new ResultHandler();
        //ansReviser = new AnswerReviser();

    }

    //LIstener que recibe el objeto resultado desde la cola
    @RabbitListener(queues = ConfigureRabbitMq.QUEUE_NAME)
    public void handleMessage2(Result res){

        ansHandler.ejecutorJava(res);
        resultRepository.save(res);

        //Enviamos el res a la cola de reviser
        new RabbitResultReviserSender(rabbitTemplate).sendMenssage(res);
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
