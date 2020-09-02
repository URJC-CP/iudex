package com.example.aplicacion.Controllers;


import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Repository.LanguageRepository;
import com.example.aplicacion.Repository.ProblemRepository;
import com.example.aplicacion.Repository.ResultRepository;
import com.example.aplicacion.Repository.SubmissionRepository;
import com.example.aplicacion.services.LanguageService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import org.apache.commons.compress.compressors.FileNameUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Controller
public class IndiceController {

    private  final RabbitTemplate rabbitTemplate;


    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private LanguageService languageService;


    //Inicio del rabbittemplate
    public IndiceController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }


    @GetMapping("/")
    public String index(Model model){
        //Pruebas de rabbit
        model.addAttribute("exercices", problemService.getAllProblemas());
        model.addAttribute("languages", languageService.getNLanguages());

        return "index";
    }

    @PostMapping("/answerSubida")
    public String subida(Model model, @RequestParam MultipartFile codigo,  @RequestParam String problemaAsignado, @RequestParam String lenguaje ) throws IOException {

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        //Crea la submission
        Submission salida = submissionService.crearPeticion(cod, problemaAsignado, lenguaje, fileName);



        model.addAttribute("comentario" , "Ha sido creado con el ID: "+salida.getId());
        return "subidaSubmission";
    }
    @GetMapping("/scoreboard")
    public String subida (Model model){

        //Cargamos la BBDD de answer en el scoreboard
        Page<Submission> listSubmiss = submissionService.getNSubmissions(10);
        model.addAttribute("submissions", listSubmiss);


        return "scoreboard";
    }

    @PostMapping("/problemSubida")
    public String subidaProblema(Model model,@RequestParam MultipartFile problema){
        try {
            problemService.addProblemFromZip(problema.getOriginalFilename(), problema.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "scoreboard";

    }
}
