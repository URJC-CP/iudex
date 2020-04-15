package com.example.aplicacion.services;

import com.example.aplicacion.Entities.Language;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Repository.LanguageRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.ResultRepository;
import com.example.aplicacion.Repository.SubmissionRepository;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

//This class Sends the propeer information to the rabbitqueu
@Service
public class SubmissionService {

    @Autowired
    private   RabbitTemplate rabbitTemplate;

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private RabbitResultExecutionSender sender;


    public String crearPeticion(String codigo,  String problem, String lenguaje ){


        //Obtedemos el Problema del que se trata
        Problem problema = problemRepository.findProblemByNombreEjercicio(problem);
        Language language  = languageRepository.findLanguageByNombreLenguaje(lenguaje);
        //Creamos la Submission
        Submission submission = new Submission(codigo, language);

        //anadimos el probelma a la submsion
        submission.setProblema(problema);

        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<String> entradasProblema = problema.getEntrada();
        List<String> salidaCorrectaProblema = problema.getSalidaCorrecta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
            Result resAux = new Result(entradasProblema.get(i), codigo, salidaCorrectaProblema.get(i), language);
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        //Guardamos la submission
        submissionRepository.save(submission);


        switch (submission.getLanguage().getNombreLenguaje()){
            case "java":
                for (Result res : submission.getResults()  ) {
                    sender.sendMessage(res);
                }
                break;

        }
        //Envio de mensaje a la cola


        return "Su peticion ha sido enviada";
    }

    public List<Submission> getNSubmissions(){
        return submissionRepository.findAll();
    }

    public List<Submission> getAllSubmissions(){
        return submissionRepository.findAll();
    }

}
