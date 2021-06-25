package com.example.aplicacion.rabbitMQ;

import com.example.aplicacion.entities.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//Clase que envia los datos a la cola
@Service
public class RabbitResultReviserSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendMenssage(Result res) {
        final var message = res;

        rabbitTemplate.convertAndSend(ConfigureRabbitMq.EXCHANGE_NAME, "dockerReviser.springmesage", message);

    }

}
