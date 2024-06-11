package es.urjc.etsii.grafo.iudex.rabbitmq;

import es.urjc.etsii.grafo.iudex.docker.DockerService;
import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.exceptions.DockerExecutionException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

//Clase que se encarga de recibir un result y mandarlo al servicio docker.
@Service
public class RabbitResultExecutionReceiver {
    private final DockerService docker;

    private final RabbitResultReviserSender rabbitResultReviserSender;

    public RabbitResultExecutionReceiver(RabbitResultReviserSender rabbitResultReviserSender,
                                         DockerService docker) {
        this.rabbitResultReviserSender = rabbitResultReviserSender;
        this.docker = docker;
    }

    //LIstener que recibe el objeto resultado desde la cola
    @RabbitListener(queues = ConfigureRabbitMq.QUEUE_NAME)
    public void handleEvaluationRequest(Result res) {
        try {
            docker.evaluate(res);
        } catch (IOException e) {
            throw new DockerExecutionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DockerExecutionException(e);
        }

        //Enviamos el res a la cola de reviser
        rabbitResultReviserSender.sendMenssage(res);
    }
}
