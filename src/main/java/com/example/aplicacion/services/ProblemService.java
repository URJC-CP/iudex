package com.example.aplicacion.services;

import com.example.aplicacion.Entities.*;
import com.example.aplicacion.Pojos.ProblemEntradaSalidaVisiblesHTML;
import com.example.aplicacion.Pojos.ProblemString;
import com.example.aplicacion.Repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProblemService {
    Logger logger = LoggerFactory.getLogger(ProblemService.class);
    @Autowired
    private ProblemRepository problemRepository;
    @Autowired
    private SampleRepository sampleRepository;
    @Autowired
    private ZipHandlerService zipHandlerService;
    @Autowired
    private ProblemValidatorService problemValidatorService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private ContestRepository contestRepository;
    @Autowired
    private SubmissionRepository submissionRepository;

    public ProblemString addProblem(Problem createdProblem) {
        logger.debug("Create new problem from problem " + createdProblem.getId());
        ProblemString problemString = new ProblemString();
        Problem newProblem = new Problem();

        updateProblemInside(newProblem, createdProblem);

        newProblem.getListaContestsPertenece().addAll(createdProblem.getListaContestsPertenece());
        newProblem.setEquipoPropietario(createdProblem.getEquipoPropietario());
        newProblem.getListaEquiposIntentados().addAll(createdProblem.getListaEquiposIntentados());

        problemRepository.save(newProblem);
        problemValidatorService.validateProblem(newProblem);

        problemString.setSalida("OK");
        problemString.setProblem(newProblem);

        logger.debug("Create build new problem " + newProblem.getId() + " from problem " + createdProblem.getId());
        return problemString;
    }

    public ProblemString addProblemFromZip(String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Create problem " + nombreProblema + " from zip " + nombreFichero + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        ProblemString salida = new ProblemString();
        Problem problem = new Problem();

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId));
        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();
        problem.setEquipoPropietario(team);

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idcontest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            salida.setSalida("CONCURSO NOT FOUND");
            return salida;
        }
        Contest contest = contestOptional.get();

        //obtener nombre del problema
        if (nombreProblema == null || nombreProblema.trim().equals("")) {
            nombreProblema = nombreFichero;
        }

        //verificar si el problema ya ha sido creado apartir del mismo zip
        Optional<Problem> problemOptional = problemRepository.findProblemByNombreEjercicio(nombreProblema);
        if (problemOptional.isPresent()) {
            problem = problemOptional.get();
            //si el problema esta almacendo en el concurso
            if (contest.getListaProblemas().contains(problem)) {
                return updateProblem(String.valueOf(problem.getId()), nombreFichero, inputStream, teamId, nombreProblema, idcontest);
            }
        }

        ProblemString problemString = zipHandlerService.generateProblemFromZIP(problem, nombreProblema, inputStream, idcontest, teamId);
        problem = problemString.getProblem();

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if (!(problemString.getSalida() == null)) {
            if (problem != null) {
                logger.error("Problem " + problem.getId() + " couldn't be saved");
            } else {
                logger.error("Couldn't create problem " + nombreProblema);
            }
            //problemRepository.deleteById(problem.getId());
            salida.setSalida(problemString.getSalida());
            return salida;
        }

        contest.addProblem(problem);
        problem.getListaContestsPertenece().add(contest);
        problemRepository.save(problem);
        contestRepository.save(contest);
        problemValidatorService.validateProblem(problem);

        salida.setProblem(problem);
        salida.setSalida("OK");
        logger.debug("Finish create problem from zip " + nombreFichero + "\nProblem name: " + problem.getNombreEjercicio() + "\nProblem id" + problem.getId() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        return salida;
    }

    public ProblemString addProblemFromZipWithoutValidate(String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Create problem " + nombreProblema + " from zip " + nombreFichero + " without validate\nTeam/user: " + teamId + "\nContest: " + idcontest);
        ProblemString salida = new ProblemString();
        Problem problem = new Problem();

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId));
        if (teamOptional.isEmpty()) {
            logger.error("Team/user " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();
        problem.setEquipoPropietario(team);

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idcontest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            salida.setSalida("CONCURSO NOT FOUND");
            return salida;
        }
        //obtener nombre del problema
        if (nombreProblema == null || nombreProblema.trim().equals("")) {
            nombreProblema = nombreFichero;
        }

        ProblemString problemString = zipHandlerService.generateProblemFromZIP(problem, nombreProblema, inputStream, idcontest, teamId);
        problem = problemString.getProblem();

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if (!(problemString.getSalida() == null)) {
            logger.error("Problem " + problem.getId() + " couldn't be saved");
            //problemRepository.deleteById(problem.getId());
            salida.setSalida(problemString.getSalida());
            return salida;
        }

        salida.setProblem(problem);
        salida.setSalida("OK");
        logger.debug("Finish create problem from zip " + nombreFichero + " without validate\nProblem name: " + problem.getNombreEjercicio() + "\nProblem id" + problem.getId() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        return salida;
    }

    public ProblemString updateProblem(String idProblema, String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Update problem " + nombreProblema + " from zip " + nombreFichero + "\nProblem id: " + idProblema + "\nContest: " + idcontest);
        ProblemString problemUpdated = new ProblemString();

        Optional<Problem> problemOriginalOptional = problemRepository.findProblemById(Long.parseLong(idProblema));
        if (problemOriginalOptional.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }
        Problem problemOriginal = problemOriginalOptional.get();

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idcontest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            problemUpdated.setSalida("CONCURSO NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(nombreFichero, inputStream, teamId, nombreProblema, idcontest);
        //Si es error
        if (!problemUpdated.getSalida().equals("OK")) {
            logger.error("Couldn't update problem " + problemOriginal.getNombreEjercicio() + "\nProblem id: " + problemOriginal.getId() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
            return problemUpdated;
        }

        //Cambiamos el id
        problemUpdated.getProblem().setId(Long.parseLong(idProblema));
        //Anyadimos al nuevo problema las submissions y problemvalidator de laanterior
        problemUpdated.getProblem().getSubmissions().addAll(problemOriginal.getSubmissions());

        //Guardams los problemvalidator viejos
        problemUpdated.getProblem().setOldSubmissionProblemValidators(problemOriginal.getOldSubmissionProblemValidators());
        problemUpdated.getProblem().getOldSubmissionProblemValidators().addAll(problemOriginal.getSubmissionProblemValidators());

        //Ponemos los participantes y concursos de la anterior
        problemUpdated.getProblem().setListaEquiposIntentados(problemOriginal.getListaEquiposIntentados());
        problemUpdated.getProblem().setListaContestsPertenece(problemOriginal.getListaContestsPertenece());

        //ACTIALIZAMOS EN LA BBDD
        problemRepository.save(problemUpdated.getProblem());
        problemValidatorService.validateProblem(problemUpdated.getProblem());

        logger.debug("Finish update problem " + idProblema + "\nProblem name: " + problemOriginal.getNombreEjercicio() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        return problemUpdated;
    }

    public ProblemString updateProblem2(String idProblema, String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Update(v2) problem " + nombreProblema + " from zip " + nombreFichero + "\nProblem id: " + idProblema + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        ProblemString problemUpdated = new ProblemString();

        Optional<Problem> problemOriginalOptional = problemRepository.findProblemById(Long.parseLong(idProblema));
        if (problemOriginalOptional.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }
        Problem problemOriginal = problemOriginalOptional.get();

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idcontest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            problemUpdated.setSalida("CONCURSO NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(nombreFichero, inputStream, teamId, nombreProblema, idcontest);
        //Si es error
        if (!problemUpdated.getSalida().equals("OK")) {
            logger.error("Couldn't update(v2) problem " + problemOriginal.getNombreEjercicio() + "\nProblem id: " + idProblema + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
            return problemUpdated;
        }

        updateProblemInside(problemOriginal, problemUpdated.getProblem());
        problemRepository.save(problemOriginal);
        problemValidatorService.validateProblem(problemOriginal);
        problemUpdated.setProblem(problemOriginal);

        logger.debug("Finish update problem " + idProblema + "\nProblem name: " + problemOriginal.getNombreEjercicio() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        return problemUpdated;
    }

    public String deleteProblem(String problemId) {
        logger.debug("Delete problem " + problemId);
        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem " + problemId + " not found");
            return "PROBLEM NOT FOUND";
        }
        Problem problem = problemOptional.get();

        //Quitamos los problemas del contest
        for (Contest contestAux : problem.getListaContestsPertenece()) {
            logger.debug("Remove problem " + problemId + " from contest " + contestAux.getId());
            if (!contestAux.getListaProblemas().remove(problem)) {
                logger.error("Couldn't remove problem " + problemId + " from contest " + contestAux.getId());
            }
        }

        // Quitamos el problema de equipos Intentados
        for (Team teamAux : problem.getListaEquiposIntentados()) {
            teamAux.getListaProblemasParticipados().remove(problem);
        }

        problemRepository.delete(problem);
        logger.debug("Finish delete problem " + problemId + "\nProblem name: " + problem.getNombreEjercicio());
        return "OK";
    }

    public String deleteProblem(Problem problem) {
        logger.debug("Delete problem " + problem.getId());

        //Quitamos los problemas del contest
        for (Contest contestAux : problem.getListaContestsPertenece()) {
            logger.debug("Remove problem " + problem.getId() + " from contest " + contestAux.getId());
            if (!contestAux.getListaProblemas().remove(problem)) {
                logger.error("Couldn't remove problem " + problem.getId() + " from contest " + contestAux.getId());
            }
        }

        problemRepository.delete(problem);
        logger.debug("Finish delete problem " + problem.getId() + "\nProblem name: " + problem.getNombreEjercicio());
        return "OK";
    }

    private boolean problemDuplicated(String nombre) {
        return problemRepository.existsByNombreEjercicio(nombre);
    }

    public List<Problem> getNProblemas(int n) {
        Pageable firstPageWithTwoElements = PageRequest.of(0, n);
        return problemRepository.findAll();
    }

    public Optional<Problem> getProblem(String idProblem) {
        return problemRepository.findProblemById(Long.parseLong(idProblem));
    }

    public List<Problem> getAllProblemas() {
        List<Problem> problemas = problemRepository.findAll();
        sumatorioSubmissionProblemas(problemas);
        return problemas;
    }

    public List<Submission> getSubmissionFromProblem(Problem problem) {
        return problem.getSubmissions();
    }

    public List<Submission> getSubmissionsFromContestFromProblem(Contest contest, Problem problem) {
        List<Submission> salida = new ArrayList<>();
        for (Submission submission : problem.getSubmissions()) {
            if (submission.isEsProblemValidator()) {
                continue;
            }
            if (submission.getContest().equals(contest)) {
                salida.add(submission);
            }
        }
        return salida;
    }

    private void sumatorioSubmissionProblemas(List<Problem> problems) {
        for (Problem problem : problems) {
            problem.setNumeroSubmissions(problem.getSubmissions().size());
        }
    }

    public List<ProblemEntradaSalidaVisiblesHTML> getProblemEntradaSalidaVisiblesHTML(Problem problem) {
        List<ProblemEntradaSalidaVisiblesHTML> lista = new ArrayList<>();

        List<Sample> datosVisibles = problem.getDatosVisibles();
        for (Sample datosVisible : datosVisibles) {
            ProblemEntradaSalidaVisiblesHTML problemEntradaSalidaVisiblesHTML = new ProblemEntradaSalidaVisiblesHTML();
            problemEntradaSalidaVisiblesHTML.setSample(datosVisible);
            lista.add(problemEntradaSalidaVisiblesHTML);
        }
        return lista;
    }

    private void saveAllSamples(Problem problem) {
        sampleRepository.saveAll(problem.getData());
    }

    private void saveAllSubmissions(Problem problem) {
        for (SubmissionProblemValidator submissionProblemValidator : problem.getSubmissionProblemValidators()) {
            submissionRepository.save(submissionProblemValidator.getSubmission());
        }
    }

    public void deleteSamples(Problem problem) {
        problem.clearData();
        problemRepository.save(problem);
    }

    private void updateProblemInside(Problem oldProblem, Problem newProblem) {
        oldProblem.setNombreEjercicio(newProblem.getNombreEjercicio());
        oldProblem.setData(newProblem.getData());

        //Anyadimos losproblemsvalidator a la lista de viejos
        oldProblem.getOldSubmissionProblemValidators().addAll(oldProblem.getSubmissionProblemValidators());
        oldProblem.setSubmissionProblemValidators(newProblem.getSubmissionProblemValidators());

        //actualizmaos el problema de submissions
        for (Submission submission : newProblem.getSubmissions()) {
            submission.setProblema(oldProblem);
        }
        oldProblem.getSubmissions().addAll(newProblem.getSubmissions());

        oldProblem.setEquipoPropietario(newProblem.getEquipoPropietario());

        oldProblem.setValido(newProblem.getValido());
        oldProblem.setTimeout(newProblem.getTimeout());
        oldProblem.setMemoryLimit(newProblem.getMemoryLimit());
        oldProblem.setAutor(newProblem.getAutor());
        oldProblem.setSource(newProblem.getSource());
        oldProblem.setLicense(newProblem.getLicense());
        oldProblem.setRights_owner(newProblem.getRights_owner());
        oldProblem.setDocumento(newProblem.getDocumento());
        oldProblem.setValidation(newProblem.getValidation());
        oldProblem.setValidation_flags(newProblem.getValidation_flags());
        oldProblem.setLimit_time_multiplier(newProblem.getLimit_time_multiplier());
        oldProblem.setLimit_time_safety_margin(newProblem.getLimit_time_safety_margin());
        oldProblem.setLimit_memory(newProblem.getLimit_memory());
        oldProblem.setLimit_output(newProblem.getLimit_output());
        oldProblem.setLimit_code(newProblem.getLimit_code());
        oldProblem.setLimit_compilation_time(newProblem.getLimit_compilation_time());
        oldProblem.setLimit_validation_memory(newProblem.getLimit_validation_memory());
        oldProblem.setLimit_validation_output(newProblem.getLimit_validation_output());
        oldProblem.setColor(newProblem.getColor());
    }

    public ProblemString updateProblemMultipleOptionalParams(String idproblem, Optional<String> nombreProblema, Optional<String> teamId, Optional<byte[]> pdf, Optional<String> timeout) {
        ProblemString salida = new ProblemString();

        Optional<Problem> problemOptional = getProblem(idproblem);
        if (problemOptional.isEmpty()) {
            logger.error("Problem " + idproblem + " not found");
            salida.setSalida("ERROR PROBLEMID NOT FOUND");
            return salida;
        }
        Problem problem = problemOptional.get();

        if (nombreProblema.isPresent()) {
            problem.setNombreEjercicio(nombreProblema.get());
        }
        if (teamId.isPresent()) {
            Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId.get()));
            if (teamOptional.isEmpty()) {
                logger.error("Team/user " + teamId + " not found");
                salida.setSalida("ERROR TEAM ID NOT FOUND");
                return salida;
            }
            Team team = teamOptional.get();
            problem.setEquipoPropietario(team);
        }

        if (pdf.isPresent()) {
            problem.setDocumento(pdf.get());
        }

        if (timeout.isPresent()) {
            problem.setTimeout(timeout.get());
        }
        problemRepository.save(problem);

        salida.setSalida("OK");
        salida.setProblem(problem);
        return salida;
    }

    public Page<Problem> getProblemsPage(Pageable pageable) {
        return problemRepository.findAll(pageable);
    }

    public String addSampleToProblem(String problemId, String name, String inputText, String outputText, boolean isPublic) {
        logger.debug("Adding new sample to problem " + problemId);
        String salida;

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem " + problemId + " not found!");
            salida = "PROBLEM NOT FOUND!";
            return salida;
        }
        Problem problem = problemOptional.get();

        if (name.trim().equals("")) {
            logger.error("Sample name is missing!");
            salida = "REQUIRED PARAMETER NAME MISSING!";
            return salida;
        }

        Sample sample = new Sample(name, inputText, outputText, isPublic);
        sampleRepository.save(sample);
        problem.addData(sample);
        problemRepository.save(problem);

        logger.debug("Finish add new sample to problem " + problemId);
        salida = "OK";
        return salida;
    }

    public String updateSampleFromProblem(Optional<String> nameOptional, String problemId, String sampleId, Optional<String> inputTextOptional, Optional<String> outputTextOptional, Optional<Boolean> isPublicOptional) {
        logger.debug("Update sample " + sampleId + " from problem " + problemId);
        ProblemString ps = new ProblemString();
        String salida;

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem " + problemId + " not found!");
            salida = "PROBLEM NOT FOUND!";
            return salida;
        }
        Problem problem = problemOptional.get();

        Optional<Sample> sampleOptional = sampleRepository.findById(Long.valueOf(sampleId));
        if (sampleOptional.isEmpty()) {
            logger.error("Sample " + sampleId + " not found!");
            salida = "SAMPLE NOT FOUND!";
            return salida;
        }
        Sample sample = sampleOptional.get();

        if (!problem.getData().contains(sample)) {
            logger.error("Sample " + sampleId + " not in problem " + problemId + "!");
            salida = "SAMPLE NOT IN PROBLEM!";
            return salida;
        }

        String name = (nameOptional.isPresent()) ? nameOptional.get() : sample.getName();
        String inputText = (inputTextOptional.isPresent()) ? inputTextOptional.get() : sample.getInputText();
        String outputText = (outputTextOptional.isPresent()) ? outputTextOptional.get() : sample.getOutputText();
        boolean isPresent = (isPublicOptional.isPresent()) ? isPublicOptional.get() : sample.isPublic();
        Sample newSample = new Sample(name, inputText, outputText, isPresent);
        sampleRepository.save(newSample);

        problem.removeData(sample);
        problem.addData(newSample);
        problemRepository.save(problem);

        logger.debug("Finish update sample " + sampleId + " from problem " + problemId);
        salida = "OK";
        return salida;
    }

    public String deleteSampleFromProblem(String problemId, String sampleId) {
        logger.debug("Adding new sample to problem " + problemId);
        String salida;

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem " + problemId + " not found!");
            salida = "PROBLEM NOT FOUND!";
            return salida;
        }
        Problem problem = problemOptional.get();

        Optional<Sample> sampleOptional = sampleRepository.findById(Long.valueOf(sampleId));
        if (sampleOptional.isEmpty()) {
            logger.error("Sample " + sampleId + " not found!");
            salida = "SAMPLE NOT FOUND!";
            return salida;
        }
        Sample sample = sampleOptional.get();

        if (!problem.getData().contains(sample)) {
            logger.error("Sample " + sampleId + " not in problem " + problemId + "!");
            salida = "SAMPLE NOT IN PROBLEM!";
            return salida;
        }

        problem.removeData(sample);
        problemRepository.save(problem);

        logger.debug("Finish delete sample " + sampleId + " from problem " + problemId);
        salida = "OK";
        return salida;
    }
}
