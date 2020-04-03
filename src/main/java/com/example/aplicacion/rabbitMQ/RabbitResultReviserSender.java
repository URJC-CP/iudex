package com.example.aplicacion.rabbitMQ;


import com.example.aplicacion.Entities.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


//Clase que envia los datos a la cola
@Service
public class RabbitResultReviserSender {

    private final RabbitTemplate rabbitTemplate;


    public RabbitResultReviserSender(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMenssage (Result res){
        final var message = res;

        rabbitTemplate.convertAndSend(ConfigureRabbitMq.EXCHANGE_NAME, "dockerReviser.springmesage", message);


    }

}
