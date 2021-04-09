package com.example.aplicacion.Controllers.standarControllers;

import com.example.aplicacion.Entities.Contest;
import com.example.aplicacion.Entities.Problem;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Pojos.SubmissionStringResult;
import com.example.aplicacion.services.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Optional;

@Controller
public class ProblemController {

    Logger logger = LoggerFactory.getLogger(ProblemController.class);
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
    public ModelAndView goToProblem(@PathVariable String idContest, @PathVariable String idProblem) {
        ModelAndView modelAndView = new ModelAndView();

        logger.debug("Get request received for problem " + idProblem + " in contest " + idContest);
        Optional<Problem> problem = problemService.getProblem(idProblem);
        Optional<Contest> contest = contestService.getContest(idContest);
        if (contest.isEmpty()) {
            logger.error("Contest " + idContest + " not found");
            modelAndView.getModel().put("error", "ERROR CONCURSO NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        if (problem.isEmpty()) {
            logger.error("Problem " + idProblem + " not found");
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        if (!contest.get().getListaProblemas().contains(problem.get())) {
            logger.error("Problem " + idProblem + " not found in contest " + idContest);
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO PERTENECE A CONCURSO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.getModel().put("problem", problem.get());
        modelAndView.getModel().put("contest", contest.get());
        modelAndView.getModel().put("languages", languageService.getNLanguages());
        modelAndView.getModel().put("teams", teamService.getAllTeams());
        modelAndView.getModel().put("ejemplos", problemService.getProblemEntradaSalidaVisiblesHTML(problem.get()));

        logger.debug("Show problem " + idProblem + " from contest " + idContest);
        modelAndView.setViewName("problem");

        return modelAndView;
    }

    //Controller que devuelve en un HTTP el pdf del problema pedido
    @GetMapping("getPDF/contest/{idContest}/problema/{idProblem}")
    public ResponseEntity<byte[]> goToProblem2(Model model, @PathVariable String idContest, @PathVariable String idProblem) {
        logger.debug("Get request received for problem " + idProblem + " in contest " + idContest);
        Optional<Problem> problem = problemService.getProblem(idProblem);

        if (problem.isEmpty()) {
            logger.error("Problem " + idProblem + " not found");
            return new ResponseEntity("ERROR PROBLEMA NO ECONTRADO", HttpStatus.NOT_FOUND);
        }


        byte[] contents = problem.get().getDocumento();
        if (contents == null || contents.length == 0) {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = problem.get().getNombreEjercicio() + ".pdf";

        //headers.setContentDispositionFormData(filename, filename);
        headers.setContentDisposition(ContentDisposition.builder("inline").build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        logger.debug("Return pdf of problem " + idProblem + " in contest " + idContest);
        return response;
    }

    @PostMapping("/problemSubida")
    public ModelAndView subidaProblema(Model model, @RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId) throws Exception {
        logger.debug("Upload request received for problem " + problemaName + " in contest " + contestId);
        ModelAndView modelAndView = new ModelAndView();
        ProblemString salida = problemService.addProblemFromZip(file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);

        if (!salida.getSalida().equals("OK")) {
            logger.error("Upload request failed with " + salida.getSalida() + "\nProblem name: " + problemaName + "\nTeam/user: " + teamId + "\nContest: " + contestId);
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        logger.debug("Upload request success\nProblem id: " + salida.getProblem().getId() + "\nProblem name: " + problemaName + "\nTeam/user: " + teamId + "\nContest: " + contestId);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @PostMapping("/problemUpdate")
    public ModelAndView updateProblema(@RequestParam String problemId, @RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId) throws Exception {
        logger.debug("Update request received for problem " + problemId + "\nProblem name: " + problemaName + "\nTeam/user: " + teamId + "\nContest: " + contestId);
        ModelAndView modelAndView = new ModelAndView();
        ProblemString salida = problemService.updateProblem2(problemId, file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);

        if (!salida.getSalida().equals("OK")) {
            logger.error("Update request failed for problem " + problemId + " with " + salida.getSalida() + "\nProblem name: " + problemaName + "\nTeam/user: " + teamId + "\nContest: " + contestId);
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        logger.debug("Update request success\nProblem id: " + salida.getProblem().getId() + "\nProblem name: " + problemaName + "\nTeam/user: " + teamId + "\nContest: " + contestId);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @GetMapping("/deleteProblem/{problemId}")
    public ModelAndView deleteProblem(@PathVariable String problemId) {
        logger.debug("Delete request received for problem " + problemId);
        ModelAndView modelAndView = new ModelAndView();
        String salida = problemService.deleteProblem(problemId);

        if (!salida.equals("OK")) {
            logger.error("Delete request failed for problem " + problemId + " with " + salida);
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        logger.debug("Delete request success for problem " + problemId);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @PostMapping("/createSubmission")
    public ModelAndView crearSubmission(@RequestParam MultipartFile codigo, @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String contestId) throws IOException {
        logger.debug("Add submission for problem " + problemaAsignado + " in contest " + contestId + "\nTeam/user: " + teamId + "\nLanguage: " + lenguaje);
        ModelAndView modelAndView = new ModelAndView();

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        //Crea la submission
        SubmissionStringResult salida = submissionService.creaYejecutaSubmission(cod, problemaAsignado, lenguaje, fileName, contestId, teamId);

        if (!salida.getSalida().equals("OK")) {
            logger.warn("Submission failed with " + salida.getSalida() + "\nContest: " + contestId + "\nProblem: " + problemaAsignado + "\nTeam/user: " + teamId + "\nLanguage: " + lenguaje);
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        logger.debug("Add submission success for problem " + problemaAsignado + " in contest " + contestId + "\nTeam/user: " + teamId + "\nLanguage: " + lenguaje);
        modelAndView.setViewName("redirect:/contest/" + contestId + "/problema/" + problemaAsignado);
        return modelAndView;
    }

    @PostMapping("/deleteSubmission")
    public ModelAndView deleteSubmission(@RequestParam String submissionId) {
        logger.debug("Delete request received for submission " + submissionId);
        ModelAndView modelAndView = new ModelAndView();

        String salida = submissionService.deleteSubmission(submissionId);
        if (!salida.equals("OK")) {
            logger.debug("Delete request failed for submission " + submissionId + " with " + salida);
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        logger.debug("Delete request success for submission " + submissionId);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }
}
