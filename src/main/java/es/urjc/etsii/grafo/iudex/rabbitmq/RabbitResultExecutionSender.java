package es.urjc.etsii.grafo.iudex.rabbitmq;


import es.urjc.etsii.grafo.iudex.entities.Result;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

//Clase que se encarga de enviar un result a la cola
@Service
public class RabbitResultExecutionSender {

    private final RabbitTemplate rabbitTemplate;

    public RabbitResultExecutionSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    public void sendMessage(Result res) {
        final var message = res;

        rabbitTemplate.convertAndSend(ConfigureRabbitMq.EXCHANGE_NAME, "dockerExecution.springmesage", message);

    }

}
