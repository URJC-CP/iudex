package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Pojos.ProblemAPI;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.example.aplicacion.services.ContestService;
import com.example.aplicacion.services.ProblemService;
import com.example.aplicacion.services.SubmissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class APIProblemController {

    @Autowired
    SubmissionService submissionService;
    @Autowired
    ContestService contestService;
    @Autowired
    ProblemService problemService;



    //PROBLEMS

    //Get all problems in DB
    @ApiOperation("Return All Problems")
    @GetMapping("/API/v1/problem")
    public ResponseEntity<List<ProblemAPI>> problems(){
        List<Problem> problems = problemService.getAllProblemas();
        List<ProblemAPI> salida = new ArrayList<>();
        for(Problem problem: problems){
            salida.add(problem.toProblemAPI());
        }
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }


    //GetProblem
    @ApiOperation("Return selected problem")
    @GetMapping("/API/v1/problem/{problemId}")
    public ResponseEntity<ProblemAPI> getProblem(@PathVariable String problemId){
        Problem problem = problemService.getProblem(problemId);
        if(problem == null){
            return new ResponseEntity("ERROR PROBLEM NOT FOUND", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(problem.toProblemAPIFull(), HttpStatus.OK);
    }

    @PostMapping("/API/v1/problem")
    public ResponseEntity<ProblemAPI> createProblem(@RequestParam Problem problem){
        throw new UnsupportedOperationException("NO implementado");
    }

    //Crea problema y devuelve el problema. Necesita team y contest
    @ApiOperation("Create Problem from Zip")
    @PostMapping("/API/v1/problem/fromZip")
    public ResponseEntity<ProblemAPI> createProblemFromZip(@RequestParam MultipartFile file, @RequestParam String problemId, @RequestParam String teamId, @RequestParam String contestId)  {
        ProblemString salida;
        try {
            salida = problemService.addProblemFromZip(file.getOriginalFilename(), file.getInputStream(), teamId, problemId, contestId);
        } catch (Exception e) {
            return new ResponseEntity("ERROR IN FILE", HttpStatus.NOT_ACCEPTABLE);
        }
        if(salida.getSalida().equals("OK")){
            return new ResponseEntity<>(salida.getProblem().toProblemAPI(), HttpStatus.OK);
        }
        else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Update problem from ZIP")
    @PutMapping("/API/v1/problem/{problemId}/fromZip")
    public ResponseEntity<ProblemAPI>updateProblem(@PathVariable String problemId, @RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId){

        ProblemString salida = new ProblemString();
        try {
            salida = problemService.updateProblem(problemId, file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);
        } catch (Exception e) {
            return new ResponseEntity("ERROR IN FILE", HttpStatus.NOT_ACCEPTABLE);
        }

        if(salida.getSalida().equals("OK")){
            return new ResponseEntity<>(salida.getProblem().toProblemAPI(), HttpStatus.OK);
        }
        //ERROR
        return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
    }


    //Devuelve el pdf del problema
    //Controller que devuelve en un HTTP el pdf del problema pedido
    @ApiOperation("Get pdf from Problem")
    @GetMapping("/API/v1//problem/{idProblem}/getPDF")
    public ResponseEntity<byte[]> goToProblem2(@PathVariable String idProblem){
        Problem problem = problemService.getProblem(idProblem);

        if(problem==null){
            return  new ResponseEntity("ERROR PROBLEMA NO ECONTRADO", HttpStatus.NOT_FOUND);
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

    @ApiOperation("Delete problem from all contests")
    @DeleteMapping("/API/v1/problem/{problemId}")
    public ResponseEntity deleteProblem(@PathVariable String problemId) {
        String salida = problemService.deleteProblem(problemId);
        if (salida.equals("OK")){
            return new  ResponseEntity(HttpStatus.OK);
        }
        else {
            return  new ResponseEntity(salida, HttpStatus.NOT_FOUND);
        }
    }


}
