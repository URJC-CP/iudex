package com.example.aplicacion.Controllers;

import com.example.aplicacion.Entities.Concurso;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.services.*;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.rule.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class ProblemaController {

    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private ConcursoService concursoService;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;


    @GetMapping("/concurso/{idConcurso}/problema/{idProblem}")
    public ModelAndView goToProblem( @PathVariable String idConcurso, @PathVariable String idProblem){
        ModelAndView modelAndView = new ModelAndView();

        Problem problem = problemService.getProblem(idProblem);
        Concurso concurso = concursoService.getConcurso(idConcurso);
        if(concurso==null){
            modelAndView.getModel().put("error", "ERROR CONCURSO NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        if(problem==null){
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        if(!concurso.getListaProblemas().contains(problem)){
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO PERTENECE A CONCURSO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.getModel().put("problem", problem);
        modelAndView.getModel().put("concurso", concurso);
        modelAndView.getModel().put("languages", languageService.getNLanguages());
        modelAndView.getModel().put("teams", teamService.getAllTeams());
        modelAndView.setViewName("problem");

        return modelAndView;
    }

    //Controller que devuelve en un HTTP el pdf del problema pedido
    @GetMapping("getPDF/concurso/{idConcurso}/problema/{idProblem}")
    public ResponseEntity<byte[]> goToProblem2(Model model, @PathVariable String idConcurso, @PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);
        Concurso concurso = concursoService.getConcurso(idConcurso);
        if(concurso==null){
            model.addAttribute("error", "ERROR CONCURSO NO ECONTRADO");
            //return "errorConocido";
        }
        if(problem==null){
            model.addAttribute("error", "ERROR PROBLEMA NO ECONTRADO");
            //return "errorConocido";
        }
        if(!concurso.getListaProblemas().contains(problem)){
            model.addAttribute("error", "ERROR PROBLEMA NO PERTENECE A CONCURSO");
            //return "errorConocido";

        }

        byte[] contents = problem.getDocumento();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = problem.getNombreEjercicio()+".pdf";
        //headers.setContentDispositionFormData(filename, filename);
        headers.setContentDisposition(ContentDisposition.builder("inline").build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return response;

    }

    @PostMapping("/problemSubida")
    public ModelAndView subidaProblema(Model model, @RequestParam MultipartFile problema, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String concursoId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        String salida = problemService.addProblemFromZip(problema.getOriginalFilename(), problema.getInputStream(), teamId, problemaName, concursoId);

        if(!salida.equals("OK")){
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/");
        return modelAndView;

    }
    @GetMapping("/deleteProblem/{problemId}")
    public ModelAndView deleteProblem(@PathVariable String problemId ){
        ModelAndView modelAndView = new ModelAndView();
        String salida = problemService.deleteProblem(problemId);

        if(!salida.equals("OK")){
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @PostMapping("/createSubmission")
    public ModelAndView subida(Model model, @RequestParam MultipartFile codigo,  @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String concursoId) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        //Crea la submission
        String salida = submissionService.creaYejecutaSubmission(cod, problemaAsignado, lenguaje, fileName, concursoId, teamId);

        if(!salida.equals("OK")){
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/concurso/"+concursoId+"/problema/"+ problemaAsignado);
        return modelAndView;
    }

}
