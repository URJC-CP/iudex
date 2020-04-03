package com.example.aplicacion.rabbitMQ;


import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Repository.ResultRepository;
import com.example.aplicacion.services.ResultReviser;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


//Clase que se encarga de recibir el
@Service
public class RabbitResultRevieserReceiver {

    @Autowired
    private ResultRepository resultRepository;


    @RabbitListener(queues = ConfigureRabbitMq.QUEUE_NAME2)
    public void handleMessage(Result res){

        //enviamos el res a revisar
        new ResultReviser().revisar(res);

        //lo guardamos
        resultRepository.save(res);
    }
}
