package com.example.aplicacion.rabbitMQ;


import com.example.aplicacion.Entities.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Clase que se encarga de enviar un result a la cola
@Service
public class RabbitResultExecutionSender {

    @Autowired
    private   RabbitTemplate rabbitTemplate;


    public void sendMessage( Result res){
        final var message = res;

        rabbitTemplate.convertAndSend(ConfigureRabbitMq.EXCHANGE_NAME, "dockerExecution.springmesage", message);

    }

}
