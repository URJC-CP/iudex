package com.example.aplicacion.rabbitMQ;

import org.springframework.stereotype.Service;


@Service

public class ReceieverRabbit {
    public void handleMessage(String mensaje){
        System.out.println(mensaje);
    }
}
