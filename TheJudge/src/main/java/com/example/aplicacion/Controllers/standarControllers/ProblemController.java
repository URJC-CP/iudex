package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.services.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class ProblemController {

    @Autowired
    private SubmissionService submissionService;
    @Autowired
    private ProblemService problemService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private ContestService contestService;
    @Autowired
    private UserService userService;
    @Autowired
    private TeamService teamService;


    @GetMapping("/contest/{idContest}/problema/{idProblem}")
    public ModelAndView goToProblem( @PathVariable String idContest, @PathVariable String idProblem){
        ModelAndView modelAndView = new ModelAndView();

        Problem problem = problemService.getProblem(idProblem);
        Contest contest = contestService.getContest(idContest);
        if(contest ==null){
            modelAndView.getModel().put("error", "ERROR CONCURSO NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        if(problem==null){
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        if(!contest.getListaProblemas().contains(problem)){
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO PERTENECE A CONCURSO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.getModel().put("problem", problem);
        modelAndView.getModel().put("contest", contest);
        modelAndView.getModel().put("languages", languageService.getNLanguages());
        modelAndView.getModel().put("teams", teamService.getAllTeams());
        modelAndView.getModel().put("ejemplos", problemService.getProblemEntradaSalidaVisiblesHTML(problem));

        modelAndView.setViewName("problem");

        return modelAndView;
    }

    //Controller que devuelve en un HTTP el pdf del problema pedido
    @GetMapping("getPDF/contest/{idContest}/problema/{idProblem}")
    public ResponseEntity<byte[]> goToProblem2(Model model, @PathVariable String idContest, @PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);
//        Contest contest = contestService.getContest(idContest);
//        if(contest ==null){
//            return  new ResponseEntity("ERROR CONCURSO NO ENCONTRADO", HttpStatus.NOT_FOUND);
//        }
        if(problem==null){
            return  new ResponseEntity("ERROR PROBLEMA NO ECONTRADO", HttpStatus.NOT_FOUND);
        }
//        if(!contest.getListaProblemas().contains(problem)){
//            return  new ResponseEntity("ERROR PROBLEMA NO PERTENCE A CONCURSO", HttpStatus.NOT_FOUND);
//        }

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
    public ModelAndView subidaProblema(Model model, @RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        ProblemString salida = problemService.addProblemFromZip(file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);

        if(!salida.getSalida().equals("OK")){
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/");
        return modelAndView;

    }
    @PostMapping("/problemUpdate")
    public ModelAndView updateProblema(@RequestParam String problemId, @RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        ProblemString salida = problemService.updateProblem2(problemId, file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);

        if(!salida.getSalida().equals("OK")){
            modelAndView.getModel().put("error", salida.getSalida());
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
    public ModelAndView crearSubmission( @RequestParam MultipartFile codigo,  @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String contestId) throws IOException {
        ModelAndView modelAndView = new ModelAndView();

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        //Crea la submission
        SubmissionStringResult salida = submissionService.creaYejecutaSubmission(cod, problemaAsignado, lenguaje, fileName, contestId, teamId);

        if(!salida.getSalida().equals("OK")){
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/contest/"+contestId+"/problema/"+ problemaAsignado);
        return modelAndView;
    }

    @PostMapping("/deleteSubmission")
    public ModelAndView deleteSubmission(@RequestParam String submissionId){
        ModelAndView modelAndView = new ModelAndView();

        String salida = submissionService.deleteSubmission(submissionId);


        if(!salida.equals("OK")){
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.setViewName("redirect:/");
        return modelAndView;

    }

}
