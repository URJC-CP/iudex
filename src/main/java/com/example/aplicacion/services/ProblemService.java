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
        ProblemString problemString = new ProblemString();

        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId));
        if (team.isEmpty()) {
            logger.error("Team/user " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        problem.setEquipoPropietario(team.get());

        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if (contest.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            salida.setSalida("CONCURSO NOT FOUND");
            return salida;
        }

        //obtener nombre del problema
        if (nombreProblema == null || nombreProblema.trim().equals("")) {
            nombreProblema = nombreFichero;
        }

        //verificar si el problema ya ha sido creado apartir del mismo zip
        Optional<Problem> aux = problemRepository.findProblemByNombreEjercicio(nombreProblema);
        if (aux.isPresent()) {
            problem = aux.get();
            //si el problema esta almacendo en el concurso
            if (contest.get().getListaProblemas().contains(aux.get())) {
                return updateProblem(String.valueOf(problem.getId()), nombreFichero, inputStream, teamId, nombreProblema, idcontest);
            }
        }

        problemString = zipHandlerService.generateProblemFromZIP(problem, nombreProblema, inputStream, idcontest, teamId);
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

        contest.get().addProblem(problem);
        problem.getListaContestsPertenece().add(contest.get());
        problemRepository.save(problem);
        contestRepository.save(contest.get());

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
        ProblemString problemString = new ProblemString();

        Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId));
        if (team.isEmpty()) {
            logger.error("Team/user " + teamId + " not found");
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }

        problem.setEquipoPropietario(team.get());
        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if (contest.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            salida.setSalida("CONCURSO NOT FOUND");
            return salida;
        }

        //obtener nombre del problema
        if (nombreProblema == null || nombreProblema.trim().equals("")) {
            nombreProblema = nombreFichero;
        }

        problemString = zipHandlerService.generateProblemFromZIP(problem, nombreProblema, inputStream, idcontest, teamId);
        problem = problemString.getProblem();

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if (!(problemString.getSalida() == null)) {
            logger.error("Problem " + problem.getId() + " couldn't be saved");
            //problemRepository.deleteById(problem.getId());
            salida.setSalida(problemString.getSalida());
            return salida;
        }

        //contest.addProblem(problem);
        //problem.getListaContestsPertenece().add(contest);

        //problemValidatorService.validateProblem(problem);
        //Devolvemos la version actualizada despues de los save
        //problem =problemRepository.findProblemById(problem.getId());

        salida.setProblem(problem);
        salida.setSalida("OK");
        logger.debug("Finish create problem from zip " + nombreFichero + " without validate\nProblem name: " + problem.getNombreEjercicio() + "\nProblem id" + problem.getId() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        return salida;
    }

    public ProblemString updateProblem(String idProblema, String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Update problem " + nombreProblema + " from zip " + nombreFichero + "\nProblem id: " + idProblema + "\nContest: " + idcontest);
        ProblemString problemUpdated = new ProblemString();

        Optional<Problem> problemOriginal = problemRepository.findProblemById(Long.parseLong(idProblema));
        if (problemOriginal.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }

        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if (contest.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            problemUpdated.setSalida("CONCURSO NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(nombreFichero, inputStream, teamId, nombreProblema, idcontest);
        //Si es error
        if (!problemUpdated.getSalida().equals("OK")) {
            logger.error("Couldn't update problem " + problemOriginal.get().getNombreEjercicio() + "\nProblem id: " + problemOriginal.get().getId() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
            return problemUpdated;
        }

        //Cambiamos el id
        problemUpdated.getProblem().setId(Long.parseLong(idProblema));
        //Anyadimos al nuevo problema las submissions y problemvalidator de laanterior
        problemUpdated.getProblem().getSubmissions().addAll(problemOriginal.get().getSubmissions());

        //Guardams los problemvalidator viejos
        problemUpdated.getProblem().setOldSubmissionProblemValidators(problemOriginal.get().getOldSubmissionProblemValidators());
        problemUpdated.getProblem().getOldSubmissionProblemValidators().addAll(problemOriginal.get().getSubmissionProblemValidators());

        //Ponemos los participantes y concursos de la anterior
        problemUpdated.getProblem().setListaEquiposIntentados(problemOriginal.get().getListaEquiposIntentados());
        problemUpdated.getProblem().setListaContestsPertenece(problemOriginal.get().getListaContestsPertenece());

        //ACTIALIZAMOS EN LA BBDD
        problemRepository.save(problemUpdated.getProblem());

        problemValidatorService.validateProblem(problemUpdated.getProblem());

        logger.debug("Finish update problem " + idProblema + "\nProblem name: " + problemOriginal.get().getNombreEjercicio() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        return problemUpdated;
    }

    public ProblemString updateProblem2(String idProblema, String nombreFichero, InputStream inputStream, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Update(v2) problem " + nombreProblema + " from zip " + nombreFichero + "\nProblem id: " + idProblema + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        ProblemString problemUpdated = new ProblemString();

        Optional<Problem> problemOriginal = problemRepository.findProblemById(Long.parseLong(idProblema));
        if (problemOriginal.isEmpty()) {
            logger.error("Problem " + idProblema + " not found");
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }

        Optional<Contest> contest = contestRepository.findContestById(Long.valueOf(idcontest));
        if (contest.isEmpty()) {
            logger.error("Contest " + idcontest + " not found");
            problemUpdated.setSalida("CONCURSO NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(nombreFichero, inputStream, teamId, nombreProblema, idcontest);
        //Si es error
        if (!problemUpdated.getSalida().equals("OK")) {
            logger.error("Couldn't update(v2) problem " + problemOriginal.get().getNombreEjercicio() + "\nProblem id: " + idProblema + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
            return problemUpdated;
        }

        updateProblemInside(problemOriginal.get(), problemUpdated.getProblem());
        //saveAllInnNOut(problemOriginal);

        problemRepository.save(problemOriginal.get());
        problemValidatorService.validateProblem(problemOriginal.get());
        problemUpdated.setProblem(problemOriginal.get());

        logger.debug("Finish update problem " + idProblema + "\nProblem name: " + problemOriginal.get().getNombreEjercicio() + "\nTeam/user: " + teamId + "\nContest: " + idcontest);
        return problemUpdated;

    }

    public String deleteProblem(String problemId) {
        logger.debug("Delete problem " + problemId);
        Optional<Problem> problem = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problem.isEmpty()) {
            logger.error("Problem " + problemId + " not found");
            return "PROBLEM NOT FOUND";
        }

        //Quitamos los problemas del contest
        for (Contest contestAux : problem.get().getListaContestsPertenece()) {
            logger.debug("Remove problem " + problemId + " from contest " + contestAux.getId());
            if (!contestAux.getListaProblemas().remove(problem.get())) {
                logger.error("Couldn't remove problem " + problemId + " from contest " + contestAux.getId());
            }
        }

        problemRepository.delete(problem.get());

        logger.debug("Finish delete problem " + problemId + "\nProblem name: " + problem.get().getNombreEjercicio());
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

    private void deleteSample(Problem problem) {
        problem.clearData();
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

        Optional<Problem> problem = getProblem(idproblem);
        if (problem.isEmpty()) {
            logger.error("Problem " + idproblem + " not found");
            salida.setSalida("ERROR PROBLEMID NOT FOUND");
            return salida;
        }

        if (nombreProblema.isPresent()) {
            problem.get().setNombreEjercicio(nombreProblema.get());
        }
        if (teamId.isPresent()) {
            Optional<Team> team = teamRepository.findTeamById(Long.valueOf(teamId.get()));
            if (team.isEmpty()) {
                logger.error("Team/user " + teamId + " not found");
                salida.setSalida("ERROR TEAMID NOT FOUND");
                return salida;
            }
            problem.get().setEquipoPropietario(team.get());
        }

        if (pdf.isPresent()) {
            problem.get().setDocumento(pdf.get());
        }

        if (timeout.isPresent()) {
            problem.get().setTimeout(timeout.get());
        }
        problemRepository.save(problem.get());

        salida.setSalida("OK");
        salida.setProblem(problem.get());
        return salida;
    }

    public Page<Problem> getProblemsPage(Pageable pageable) {
        return problemRepository.findAll(pageable);
    }
}
