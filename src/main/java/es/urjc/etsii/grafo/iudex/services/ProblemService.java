package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.*;
import es.urjc.etsii.grafo.iudex.pojos.ProblemEntradaSalidaVisiblesHTML;
import es.urjc.etsii.grafo.iudex.pojos.ProblemString;
import es.urjc.etsii.grafo.iudex.repositories.*;
import es.urjc.etsii.grafo.iudex.utils.Sanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProblemService {
    private static final Logger logger = LoggerFactory.getLogger(ProblemService.class);
    private final ContestRepository contestRepository;
    private final ProblemRepository problemRepository;
    private final TeamRepository teamRepository;
    private final SampleRepository sampleRepository;

    private final ZipHandlerService zipHandlerService;
    private final ProblemValidatorService problemValidatorService;
    private final ContestProblemService contestProblemService;
    private final ContestTeamService contestTeamService;

    public ProblemService(ContestRepository contestRepository, ProblemRepository problemRepository, TeamRepository teamRepository, SampleRepository sampleRepository, ZipHandlerService zipHandlerService, ProblemValidatorService problemValidatorService, ContestProblemService contestProblemService, ContestTeamService contestTeamService) {
        this.contestRepository = contestRepository;
        this.problemRepository = problemRepository;
        this.teamRepository = teamRepository;
        this.sampleRepository = sampleRepository;
        this.zipHandlerService = zipHandlerService;
        this.problemValidatorService = problemValidatorService;
        this.contestProblemService = contestProblemService;
        this.contestTeamService = contestTeamService;
    }

    public ProblemString addProblem(Problem createdProblem) {
        logger.debug("Create new problem from problem {}", createdProblem.getId());
        ProblemString problemString = new ProblemString();
        Problem newProblem = new Problem();

        updateProblemInside(newProblem, createdProblem);

        newProblem.getListaProblemasParticipados().addAll(createdProblem.getListaProblemasParticipados());
        newProblem.setEquipoPropietario(createdProblem.getEquipoPropietario());
        newProblem.getListaProblemasParticipados().addAll(createdProblem.getEquipoPropietario().getListaProblemasCreados());

        problemRepository.save(newProblem);
        problemValidatorService.validateProblem(newProblem);

        problemString.setSalida("OK");
        problemString.setProblem(newProblem);

        logger.debug("Create new problem {} from problem {}", newProblem.getId(), createdProblem.getId());
        return problemString;
    }

    public ProblemString addProblemFromZip(MultipartFile file, String teamId, String nombreProblema, String idcontest) throws Exception {
        ProblemString salida = new ProblemString();
        Problem problem = new Problem();
        
        logger.debug("Create problem {}", nombreProblema);

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId));
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();
        problem.setEquipoPropietario(team);

        Optional<Contest> contestOptional = contestRepository.findContestById(Long.parseLong(idcontest));
        if (contestOptional.isEmpty()) {
            logger.error("Contest {} not found", idcontest);
            salida.setSalida("CONTEST NOT FOUND");
            return salida;
        }
        Contest contest = contestOptional.get();

        //verificar si el problema ya ha sido creado apartir del mismo zip
        Optional<Problem> problemOptional = problemRepository.findProblemByNombreEjercicio(nombreProblema);
        ContestProblem contestProblem;

        if (problemOptional.isPresent()) {
            problem = problemOptional.get();

            Optional<ContestProblem> optionalContestProblem = contestProblemService.getContestProblemByContestAndProblem(contest, problem);
            if (optionalContestProblem.isEmpty()) contestProblem = new ContestProblem(contest,problem,LocalDateTime.now());
            else contestProblem = optionalContestProblem.get();

            //si el problema esta almacendo en el concurso
            if (contest.getListaProblemas().contains(contestProblem)) {
                return updateProblem(String.valueOf(problem.getId()), file, teamId, nombreProblema, idcontest);
            }
        } else {
            contestProblem = new ContestProblem(contest,problem,LocalDateTime.now());
        }

        ProblemString problemString = zipHandlerService.generateProblemFromZIP(problem, nombreProblema, file.getInputStream(), teamId, idcontest);
        problem = problemString.getProblem();

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if (problemString.getSalida() != null) {
            if (problem != null) {
                logger.error("Save problem {} with name {} failed with {}", problem.getId(), nombreProblema, problemString.getSalida());
            } else {
                logger.error("Create problem {} failed with {} ", nombreProblema, problemString.getSalida());
            }
            salida.setSalida(problemString.getSalida());
            return salida;
        }

        ContestTeams contestTeams = new ContestTeams(contest,team,LocalDateTime.now());

        contest.addProblem(contestProblem);
        problem.addContest(contestProblem);
        contestProblem.getContest().getListaContestsParticipados().add(contestTeams);
        problemRepository.save(problem);
        contestRepository.save(contest);
        problemValidatorService.validateProblem(problem);

        salida.setProblem(problem);
        salida.setSalida("OK");
        logger.debug("Finish create problem {} with name {}", problem.getId(), nombreProblema);
        return salida;
    }

    public ProblemString addProblemFromZipWithoutValidate(MultipartFile file, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Create problem {} from without validate", nombreProblema);
        ProblemString salida = new ProblemString();
        Problem problem = new Problem();

        Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId));
        if (teamOptional.isEmpty()) {
            logger.error("Team {} not found", teamId);
            salida.setSalida("TEAM NOT FOUND");
            return salida;
        }
        Team team = teamOptional.get();
        problem.setEquipoPropietario(team);

        if (contestRepository.existsContestById(Long.parseLong(idcontest))) {
            logger.error("Contest {} not found", idcontest);
            salida.setSalida("CONTEST NOT FOUND");
            return salida;
        }

        ProblemString problemString = zipHandlerService.generateProblemFromZIP(problem, nombreProblema, file.getInputStream(), teamId, idcontest);
        problem = problemString.getProblem();

        //Verificamos si hubiera dado fallo el problema al guardarse
        //SI FALLA NO SE GUARDA EL PROBLEMA
        if (problemString.getSalida() != null) {
            logger.error("Save problem {} failed with {}", problem.getId(), problemString.getSalida());
            salida.setSalida(problemString.getSalida());
            return salida;
        }

        salida.setProblem(problem);
        salida.setSalida("OK");
        logger.debug("Finish create problem {} without validate", problem.getId());
        return salida;
    }

    public ProblemString updateProblem(String idProblema, MultipartFile file, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Update problem {}", nombreProblema);
        ProblemString problemUpdated = new ProblemString();

        Optional<Problem> problemOriginalOptional = problemRepository.findProblemById(Long.parseLong(idProblema));
        if (problemOriginalOptional.isEmpty()) {
            logger.error("Problem {} not found", idProblema);
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }
        Problem problemOriginal = problemOriginalOptional.get();

        if (contestRepository.existsContestById(Long.parseLong(idcontest))) {
            logger.error("Contest {} not found", idcontest);
            problemUpdated.setSalida("CONTEST NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(file, teamId, nombreProblema, idcontest);
        //Si es error
        if (!problemUpdated.getSalida().equals("OK")) {
            logger.error("Update problem {} failed with {}", problemOriginal.getNombreEjercicio(), problemUpdated.getSalida());
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
        problemUpdated.getProblem().setListaProblemasParticipados(problemOriginal.getEquipoPropietario().getListaProblemasParticipados());
        problemUpdated.getProblem().setListaConcursos(problemOriginal.getListaConcursos());

        //ACTIALIZAMOS EN LA BBDD
        problemRepository.save(problemUpdated.getProblem());
        problemValidatorService.validateProblem(problemUpdated.getProblem());

        logger.debug("Finish update problem {} with name {} ", idProblema, problemOriginal.getNombreEjercicio());
        return problemUpdated;
    }

    public ProblemString updateProblem2(String idProblema, MultipartFile file, String teamId, String nombreProblema, String idcontest) throws Exception {
        logger.debug("Update(v2) problem {}", idProblema);
        ProblemString problemUpdated = new ProblemString();

        Optional<Problem> problemOriginalOptional = problemRepository.findProblemById(Long.parseLong(idProblema));
        if (problemOriginalOptional.isEmpty()) {
            logger.error("Problem {} not found", idProblema);
            problemUpdated.setSalida("PROBLEM NOT FOUND");
            return problemUpdated;
        }
        Problem problemOriginal = problemOriginalOptional.get();

        if (contestRepository.existsContestById(Long.parseLong(idcontest))) {
            logger.error("Contest {} not found", idcontest);
            problemUpdated.setSalida("CONTEST NOT FOUND");
            return problemUpdated;
        }

        problemUpdated = addProblemFromZipWithoutValidate(file, teamId, nombreProblema, idcontest);
        //Si es error
        if (!problemUpdated.getSalida().equals("OK")) {
            logger.error("Update problem {} failed with {}", idProblema, problemUpdated.getSalida());
            return problemUpdated;
        }

        updateProblemInside(problemOriginal, problemUpdated.getProblem());
        problemRepository.save(problemOriginal);
        problemValidatorService.validateProblem(problemOriginal);
        problemUpdated.setProblem(problemOriginal);

        logger.debug("Finish update problem {} with name {}", idProblema, nombreProblema);
        return problemUpdated;
    }

    public String deleteProblem(String problemId) {
        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", problemId);
            return "PROBLEM NOT FOUND";
        }
        Problem problem = problemOptional.get();

        return deleteProblem(problem);
    }

    public String deleteProblem(Problem problem) {
        logger.debug("Delete problem {}", problem.getId());

        //Quitamos los problemas del contest
        for (ContestProblem contestAux : problem.getListaConcursos()) {
            logger.debug("Remove problem {} from contest {}", problem.getId(), contestAux.getProblem().getId());
            if (!contestAux.getContest().getListaProblemas().remove(contestAux)) {
                logger.error("Remove problem {} from contest {} failed", problem.getId(), contestAux.getContest().getId());
            }
        }

        // Quitamos el problema de equipos Intentados
        for (TeamsProblems teamAux : problem.getListaProblemasParticipados()) {
            if (!teamAux.getTeams().getListaProblemasParticipados().remove(teamAux)) {
                logger.error("Remove problem {} from team {} failed", teamAux.getProblem().getId(), teamAux.getTeams().getId());
            }
        }

        problemRepository.delete(problem);
        logger.debug("Finish delete problem {}", problem.getId());
        return "OK";
    }

    private boolean problemDuplicated(String nombre) {
        return problemRepository.existsProblemByNombreEjercicio(nombre);
    }

    public List<Problem> getNProblemas(int n) {
        Pageable pageable = PageRequest.of(0, n);
        return problemRepository.findAll(pageable).toList();
    }

    public Optional<Problem> getProblem(String idProblem) {
        return problemRepository.findProblemById(Long.parseLong(idProblem));
    }

    public List<Problem> getAllProblemas() {
        List<Problem> problemas = problemRepository.findAll();
        sumatorioSubmissionProblemas(problemas);
        return problemas;
    }

    public Set<Submission> getSubmissionFromProblem(Problem problem) {
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

        Set<Sample> datosVisibles = problem.getDatosVisibles();
        for (Sample datosVisible : datosVisibles) {
            ProblemEntradaSalidaVisiblesHTML problemEntradaSalidaVisiblesHTML = new ProblemEntradaSalidaVisiblesHTML();
            problemEntradaSalidaVisiblesHTML.setSample(datosVisible);
            lista.add(problemEntradaSalidaVisiblesHTML);
        }
        return lista;
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
            submission.setProblem(oldProblem);
        }
        oldProblem.getSubmissions().addAll(newProblem.getSubmissions());

        oldProblem.setEquipoPropietario(newProblem.getEquipoPropietario());

        oldProblem.setValido(newProblem.getValido());
        oldProblem.setTimeout(newProblem.getTimeout());
        oldProblem.setMemoryLimit(newProblem.getMemoryLimit());
        oldProblem.setAutor(newProblem.getAutor());
        oldProblem.setSource(newProblem.getSource());
        oldProblem.setLicense(newProblem.getLicense());
        oldProblem.setOwnerRights(newProblem.getOwnerRights());
        oldProblem.setDocumento(newProblem.getDocumento());
        oldProblem.setValidation(newProblem.getValidation());
        oldProblem.setValidationFlags(newProblem.getValidationFlags());
        oldProblem.setLimitTimeMultiplier(newProblem.getLimitTimeMultiplier());
        oldProblem.setLimitTimeSafetyMargin(newProblem.getLimitTimeSafetyMargin());
        oldProblem.setLimitMemory(newProblem.getLimitMemory());
        oldProblem.setLimitOutput(newProblem.getLimitOutput());
        oldProblem.setLimitCode(newProblem.getLimitCode());
        oldProblem.setLimitCompilationTime(newProblem.getLimitCompilationTime());
        oldProblem.setLimitValidationMemory(newProblem.getLimitValidationMemory());
        oldProblem.setLimitValidationOutput(newProblem.getLimitValidationOutput());
        oldProblem.setColor(newProblem.getColor());
    }

    public ProblemString updateProblemMultipleOptionalParams(String idproblem, Optional<String> nombreProblema, Optional<String> teamId, MultipartFile pdf, Optional<String> timeout) throws IOException {
        byte[] pdfBytes = null;
        if (pdf != null) {
            pdfBytes = pdf.getBytes();
        }
        Optional<byte[]> optPdfBytes = Optional.ofNullable(pdfBytes);
        ProblemString salida = new ProblemString();

        Optional<Problem> problemOptional = getProblem(idproblem);
        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", idproblem);
            salida.setSalida("PROBLEM NOT FOUND");
            return salida;
        }
        Problem problem = problemOptional.get();

        if (nombreProblema.isPresent()) {
            problem.setNombreEjercicio(nombreProblema.get());
        }
        if (teamId.isPresent()) {
            Optional<Team> teamOptional = teamRepository.findTeamById(Long.parseLong(teamId.get()));
            if (teamOptional.isEmpty()) {
                logger.error("Team {} not found ", teamId);
                salida.setSalida("TEAM NOT FOUND");
                return salida;
            }
            Team team = teamOptional.get();
            problem.setEquipoPropietario(team);
        }

        if (optPdfBytes.isPresent()) {
            problem.setDocumento(optPdfBytes.get());
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

    public String addSampleToProblem(String problemId, String name, MultipartFile sampleInput, MultipartFile sampleOutput, boolean isPublic) throws IOException {
        String inputText = new String(sampleInput.getBytes());
        String outputText = new String(sampleOutput.getBytes());

        logger.debug("Adding new sample to problem {}", problemId);

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", problemId);
            return "PROBLEM NOT FOUND";
        }
        Problem problem = problemOptional.get();

        if (name.trim().equals("")) {
            logger.error("Sample name is missing");
            return "SAMPLE NAME NOT SPECIFIED";
        }

        Sample sample = new Sample(name, inputText, outputText, isPublic);
        sampleRepository.save(sample);
        problem.addData(sample);
        problemRepository.save(problem);

        logger.debug("Finish add new sample to problem {}", problemId);
        return "OK";
    }

    public String updateSampleFromProblem(Optional<String> nameOptional, String problemId, String sampleId, Optional<String> inputTextOptional, Optional<String> outputTextOptional, Optional<Boolean> isPublicOptional) {
        logger.debug("Update sample {} from problem {}", sampleId, problemId);

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", problemId);
            return "PROBLEM NOT FOUND";
        }
        Problem problem = problemOptional.get();

        Optional<Sample> sampleOptional = sampleRepository.findById(Long.valueOf(sampleId));
        if (sampleOptional.isEmpty()) {
            logger.error("Sample {} not found", sampleId);
            return "SAMPLE NOT FOUND";
        }
        Sample sample = sampleOptional.get();

        if (!problem.getData().contains(sample)) {
            logger.error("Sample {} not in problem {}", sampleId, problemId);
            return "SAMPLE NOT IN PROBLEM";
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

        logger.debug("Finish update sample {} from problem {}", sampleId, problemId);
        return "OK";
    }

    public String deleteSampleFromProblem(String problemId, String sampleId) {
        logger.debug("Delete sample {} from problem {}", sampleId, problemId);

        Optional<Problem> problemOptional = problemRepository.findProblemById(Long.parseLong(problemId));
        if (problemOptional.isEmpty()) {
            logger.error("Problem {} not found", problemId);
            return "PROBLEM NOT FOUND";
        }
        Problem problem = problemOptional.get();

        Optional<Sample> sampleOptional = sampleRepository.findById(Long.valueOf(sampleId));
        if (sampleOptional.isEmpty()) {
            logger.error("Sample {} not found", sampleId);
            return "SAMPLE NOT FOUND";
        }
        Sample sample = sampleOptional.get();

        if (!problem.getData().contains(sample)) {
            logger.error("Sample {} not in problem {}", sampleId, problemId);
            return "SAMPLE NOT IN PROBLEM";
        }

        problem.removeData(sample);
        problemRepository.save(problem);

        logger.debug("Finish delete sample {} from problem {}", sampleId, problemId);
        return "OK";
    }
}
