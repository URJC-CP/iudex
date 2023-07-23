package es.urjc.etsii.grafo.iudex.rabbitmq;

import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.exceptions.DockerExecutionException;
import es.urjc.etsii.grafo.iudex.services.ResultHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

//Clase que se encarga de recibir un result y mandarlo al servicio docker.
@Service
public class RabbitResultExecutionReceiver {
    @Autowired
    private ResultHandler resultHandler;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitResultReviserSender rabbitResultReviserSender;


    //LIstener que recibe el objeto resultado desde la cola
    @RabbitListener(queues = ConfigureRabbitMq.QUEUE_NAME)
    public void handleMessage2(Result res) {

        //Primero ejecutamos el codigo y guardamos
        try {
            resultHandler.ejecutor(res);
        } catch (IOException e) {
            throw new DockerExecutionException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DockerExecutionException(e);
        }
        //resultRepository.save(res);

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
