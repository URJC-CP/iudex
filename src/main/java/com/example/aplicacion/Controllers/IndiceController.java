package com.example.aplicacion.Controllers;


import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.ResultRepository;
import com.example.aplicacion.Repository.SubmissionRepository;
import com.example.aplicacion.rabbitMQ.RabbitResultExecutionSender;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class IndiceController {

    private  final RabbitTemplate rabbitTemplate;
    RabbitResultExecutionSender sender;

    @Autowired
    private SubmissionRepository submissionRepository;
    @Autowired
    public ProblemRepository problemRepository;
    @Autowired
    private ResultRepository resultRepository;

    //Inicio del rabbittemplate
    public IndiceController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        //this.rabbitTemplate = new RabbitTemplate();
    this.sender = new RabbitResultExecutionSender(rabbitTemplate);
    }
    @GetMapping("/")
    public String index(Model model){
        //Pruebas de rabbit
        List<Problem> listaEjercicios = problemRepository.findAll();
        model.addAttribute("exercices", listaEjercicios);


        return "index";
    }

    @PostMapping("/answerSubida")
    public String subida(Model model, @RequestParam MultipartFile codigo,  @RequestParam String problemaAsignado) throws IOException {

        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        String lenguaje = "java";

        //Obtedemos el Problema del que se trata
        Problem problema = problemRepository.findProblemByNombreEjercicio(problemaAsignado);

        //Creamos la Submission
        Submission submission = new Submission(cod, lenguaje);

        //anadimos el probelma a la submsion
        submission.setProblema(problema);

        //Creamos los result que tienen que ir con la submission y anadimos a submision
        List<String> entradasProblema = problema.getEntrada();
        List<String> salidaCorrectaProblema = problema.getSalidaCorrecta();
        int numeroEntradas = entradasProblema.size();
        for(int i =0; i<numeroEntradas; i++){
            Result resAux = new Result(entradasProblema.get(i), cod, salidaCorrectaProblema.get(i));
            resultRepository.save(resAux);
            submission.addResult(resAux);
        }

        //Guardamos la submission
        submissionRepository.save(submission);




        //Envio de mensaje a la cola
        for (Result res : submission.getResults()  ) {
            sender.sendMessage(res);
        }

        return "subidaSubmission";
    }
    @GetMapping("/scoreboard")
    public String subida (Model model){

        //Cargamos la BBDD de answer en el scoreboard
        List<Submission> listAns = submissionRepository.findAll();
        model.addAttribute("answers", listAns);


        return "scoreboard";
    }
}
