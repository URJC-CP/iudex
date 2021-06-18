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
        logger.debug("Get problem {0}", idProblem);

        Optional<Contest> contestOptional = contestService.getContestById(idContest);
        if (contestOptional.isEmpty()) {
            logger.error("Contest {0} not found", idContest);
            modelAndView.getModel().put("error", "ERROR CONCURSO NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        Contest contest = contestOptional.get();

        Optional<Problem> problemOptional = problemService.getProblem(idProblem);
        if (problemOptional.isEmpty()) {
            logger.error("Problem {0} not found", idProblem);
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO ECONTRADO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        Problem problem = problemOptional.get();

        if (!contest.getListaProblemas().contains(problem)) {
            logger.error("Problem {0} not in contest {1}", idProblem, idContest);
            modelAndView.getModel().put("error", "ERROR PROBLEMA NO PERTENECE A CONCURSO");
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        modelAndView.getModel().put("problem", problem);
        modelAndView.getModel().put("contest", contest);
        modelAndView.getModel().put("languages", languageService.getNLanguages());
        modelAndView.getModel().put("teams", teamService.getAllTeams());
        modelAndView.getModel().put("ejemplos", problemService.getProblemEntradaSalidaVisiblesHTML(problem));

        logger.debug("Show problem {0}", idProblem);
        modelAndView.setViewName("problem");

        return modelAndView;
    }

    //Controller que devuelve en un HTTP el pdf del problema pedido
    @GetMapping("getPDF/contest/{idContest}/problema/{idProblem}")
    public ResponseEntity<byte[]> goToProblem2(Model model, @PathVariable String idContest, @PathVariable String idProblem) {
        logger.debug("Get problem {0} with pdf", idProblem);
        Optional<Problem> problemOptional = problemService.getProblem(idProblem);

        if (problemOptional.isEmpty()) {
            logger.error("Problem {0} not found", idProblem);
            return new ResponseEntity("ERROR PROBLEMA NO ECONTRADO", HttpStatus.NOT_FOUND);
        }
        Problem problem = problemOptional.get();

        byte[] contents = problem.getDocumento();
        if (contents == null || contents.length == 0) {
            return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        String filename = problem.getNombreEjercicio() + ".pdf";

        //headers.setContentDispositionFormData(filename, filename);
        headers.setContentDisposition(ContentDisposition.builder("inline").build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        logger.debug("Return problem {0} with pdf", idProblem);
        return response;
    }

    @PostMapping("/problemSubida")
    public ModelAndView subidaProblema(Model model, @RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId) throws Exception {
        logger.debug("Upload problem {0}", problemaName);
        ModelAndView modelAndView = new ModelAndView();
        ProblemString salida = problemService.addProblemFromZip(file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);

        if (!salida.getSalida().equals("OK")) {
            logger.error("Upload problem {0} failed with {1} ", problemaName, salida.getSalida());
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        logger.debug("Upload problem {0} success", problemaName);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @PostMapping("/problemUpdate")
    public ModelAndView updateProblema(@RequestParam String problemId, @RequestParam MultipartFile file, @RequestParam String problemaName, @RequestParam String teamId, @RequestParam String contestId) throws Exception {
        logger.debug("Update problem {0}", problemId);
        ModelAndView modelAndView = new ModelAndView();
        ProblemString salida = problemService.updateProblem2(problemId, file.getOriginalFilename(), file.getInputStream(), teamId, problemaName, contestId);

        if (!salida.getSalida().equals("OK")) {
            logger.error("Update problem {0} failed with {1} ", problemId, salida.getSalida());
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        logger.debug("Update problem {0} success", problemId);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @GetMapping("/deleteProblem/{problemId}")
    public ModelAndView deleteProblem(@PathVariable String problemId) {
        logger.debug("Delete problem {0}", problemId);
        ModelAndView modelAndView = new ModelAndView();
        String salida = problemService.deleteProblem(problemId);

        if (!salida.equals("OK")) {
            logger.error("Delete problem {0} failed with", problemId, salida);
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        logger.debug("Delete problem {0} success", problemId);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }

    @PostMapping("/createSubmission")
    public ModelAndView crearSubmission(@RequestParam MultipartFile codigo, @RequestParam String problemaAsignado, @RequestParam String lenguaje, @RequestParam String teamId, @RequestParam String contestId) throws IOException {
        logger.debug("Create {0} submission for problem {1}", lenguaje, problemaAsignado);
        ModelAndView modelAndView = new ModelAndView();

        String fileNameaux = codigo.getOriginalFilename();
        String fileName = FilenameUtils.removeExtension(fileNameaux);
        String cod = new String(codigo.getBytes());
        //String ent = new String(entrada.getBytes());
        //Crea la submission
        SubmissionStringResult salida = submissionService.creaYejecutaSubmission(cod, problemaAsignado, lenguaje, fileName, contestId, teamId);

        if (!salida.getSalida().equals("OK")) {
            logger.warn("Submission failed with {0}", salida.getSalida());
            modelAndView.getModel().put("error", salida.getSalida());
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }

        logger.debug("Add submission {0} success for problem ", salida.getSubmission().getId());
        modelAndView.setViewName("redirect:/contest/" + contestId + "/problema/" + problemaAsignado);
        return modelAndView;
    }

    @PostMapping("/deleteSubmission")
    public ModelAndView deleteSubmission(@RequestParam String submissionId) {
        logger.debug("Delete submission {0}", submissionId);
        ModelAndView modelAndView = new ModelAndView();

        String salida = submissionService.deleteSubmission(submissionId);
        if (!salida.equals("OK")) {
            logger.debug("Delete submission {0} failed with {1}", submissionId, salida);
            modelAndView.getModel().put("error", salida);
            modelAndView.setViewName("errorConocido");
            return modelAndView;
        }
        logger.debug("Delete submission ", submissionId);
        modelAndView.setViewName("redirect:/");
        return modelAndView;
    }
}
