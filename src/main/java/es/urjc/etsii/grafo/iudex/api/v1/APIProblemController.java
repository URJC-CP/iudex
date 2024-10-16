package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.Problem;
import es.urjc.etsii.grafo.iudex.pojos.ProblemAPI;
import es.urjc.etsii.grafo.iudex.pojos.ProblemString;
import es.urjc.etsii.grafo.iudex.services.ProblemService;
import es.urjc.etsii.grafo.iudex.utils.Sanitizer;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.Consumes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/API/v1/")
public class APIProblemController {

    final ProblemService problemService;

    public APIProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    //Get all problems in DB
    @Operation( summary = "Return All Problems")
    @GetMapping("problem")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<List<ProblemAPI>> problems() {
        List<Problem> problems = problemService.getAllProblemas();
        List<ProblemAPI> salida = new ArrayList<>();
        for (Problem problem : problems) {
            salida.add(problem.toProblemAPI());
        }
        return new ResponseEntity<>(salida, HttpStatus.OK);
    }

    @Operation( summary = "Return Page of all Problems")
    @GetMapping("problem/page")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<Page<ProblemAPI>> getAllProblemPage(Pageable pageable) {
        return new ResponseEntity<>(problemService.getProblemsPage(pageable).map(Problem::toProblemAPI), HttpStatus.OK);
    }

    //GetProblem
    @Operation( summary = "Return selected problem")
    @GetMapping("problem/{problemId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ProblemAPI> getProblem(@PathVariable String problemId) {
        problemId = Sanitizer.removeLineBreaks(problemId);

        Optional<Problem> problemOptional = problemService.getProblem(problemId);
        if (problemOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Problem problem = problemOptional.get();
        return new ResponseEntity<>(problem.toProblemAPIFull(), HttpStatus.OK);
    }

    //Crea problema desde objeto Problem
//    @Operation( summary = "Create problem Using a Problem Object")
//    @PostMapping("problem")
//    public ResponseEntity<ProblemAPI> createProblem(@RequestParam Problem problem) {
//
//        ProblemString salida = problemService.addProblem(problem);
//        if (salida.getSalida().equals("OK")) {
//            return new ResponseEntity<>(salida.getProblem().toProblemAPI(), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    //Crea problema y devuelve el problema. Necesita team y contest
    @Operation( summary = "Create Problem from Zip")
    @PostMapping(value = "problem/fromZip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<ProblemAPI> createProblemFromZip(@RequestPart("file") MultipartFile file, @RequestParam(required = false) String problemName, @RequestParam String teamId, @RequestParam String contestId) {
        problemName = Sanitizer.removeLineBreaks(problemName);
        teamId = Sanitizer.removeLineBreaks(teamId);
        contestId = Sanitizer.removeLineBreaks(contestId);

        try {
            ProblemString salida = problemService.addProblemFromZip(file, teamId, problemName, contestId);
            if (salida.getSalida().equals("OK")) {
                return new ResponseEntity<>(salida.getProblem().toProblemAPI(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Operation( summary = "Update problem from ZIP")
    @PutMapping(value = "problem/{problemId}/fromZip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<ProblemAPI> updateProblemFromZip(@PathVariable String problemId, @RequestPart("file") MultipartFile file, @RequestParam String problemName, @RequestParam String teamId, @RequestParam String contestId) {
        problemId = Sanitizer.removeLineBreaks(problemId);
        problemName = Sanitizer.removeLineBreaks(problemName);
        teamId = Sanitizer.removeLineBreaks(teamId);
        contestId = Sanitizer.removeLineBreaks(contestId);

        try {
            ProblemString salida = problemService.updateProblem(problemId, file, teamId, problemName, contestId);
            if (salida.getSalida().equals("OK")) {
                return new ResponseEntity<>(salida.getProblem().toProblemAPI(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @Operation( summary = "Update a problem with Request Param")
    @PutMapping("problem/{problemId}")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<ProblemAPI> updateProblem(@PathVariable String problemId, @RequestParam(required = false) Optional<String> problemName, @RequestParam(required = false) Optional<String> teamId, @RequestParam(required = false) Optional<String> timeout, @RequestPart(name = "pdf", required = false) MultipartFile pdf) throws IOException {
        problemId = Sanitizer.removeLineBreaks(problemId);
        problemName = Sanitizer.removeLineBreaks(problemName);
        teamId = Sanitizer.removeLineBreaks(teamId);
        timeout = Sanitizer.removeLineBreaks(timeout);
        ProblemString salida = problemService.updateProblemMultipleOptionalParams(problemId, problemName, teamId, pdf, timeout);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getProblem().toProblemAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //Devuelve el pdf del problema
    //Controller que devuelve en un HTTP el pdf del problema pedido
    @Operation( summary = "Get pdf from Problem")
    @GetMapping("problem/{problemId}/getPDF")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<byte[]> goToProblem2(@PathVariable String problemId) {
        problemId = Sanitizer.removeLineBreaks(problemId);

        Optional<Problem> problemOptional = problemService.getProblem(problemId);
        if (problemOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Problem problem = problemOptional.get();

        byte[] contents = problem.getDocumento();
        if (contents == null || contents.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("inline").build());
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }

    @Operation( summary = "Delete problem from all contests")
    @DeleteMapping("problem/{problemId}")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<String> deleteProblem(@PathVariable String problemId) {
        problemId = Sanitizer.removeLineBreaks(problemId);

        String salida = problemService.deleteProblem(problemId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
        }
    }

    @Operation( summary = "Add sample to problem")
    @PostMapping("problem/{problemId}/sample")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<String> addSampleToProblem(@PathVariable String problemId, @RequestParam String name, @RequestPart("entrada") MultipartFile sampleInput, @RequestPart("salida") MultipartFile sampleOutput, @RequestParam boolean isPublic) {
        problemId = Sanitizer.removeLineBreaks(problemId);
        name = Sanitizer.removeLineBreaks(name);

        try {
            String salida = problemService.addSampleToProblem(problemId, name, sampleInput, sampleOutput, isPublic);

            if (salida.equals("OK")) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            return new ResponseEntity<>("ERROR IN INPUT FILE", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }


    @Operation( summary = "Update sample from problem")
    @PutMapping("problem/{problemId}/sample/{sampleId}")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<String> updateSampleFromProblem(@PathVariable String problemId, @PathVariable String sampleId, @RequestParam(value = "name", required = false) Optional<String> nameOptional, @RequestPart(value = "entrada", required = false) Optional<MultipartFile> sampleInputOptional, @RequestPart(value = "salida", required = false) Optional<MultipartFile> sampleOutputOptional, @RequestParam(value = "isPublic", required = false) Optional<Boolean> isPublicOptional) {
        problemId = Sanitizer.removeLineBreaks(problemId);
        sampleId = Sanitizer.removeLineBreaks(sampleId);
        nameOptional = Sanitizer.removeLineBreaks(nameOptional);

        if (nameOptional.isEmpty() && sampleInputOptional.isEmpty() && sampleOutputOptional.isEmpty() && isPublicOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<String> inputText = Optional.empty();
        try {
            if (sampleInputOptional.isPresent()) {
                inputText = Optional.of(new String(sampleInputOptional.get().getBytes()));
            }
        } catch (IOException e) {
            return new ResponseEntity<>("ERROR IN INPUT FILE", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        Optional<String> outputText = Optional.empty();
        try {
            if (sampleOutputOptional.isPresent()) {
                outputText = Optional.of(new String(sampleOutputOptional.get().getBytes()));
            }
        } catch (IOException e) {
            return new ResponseEntity<>("ERROR IN OUTPUT FILE", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        String salida = problemService.updateSampleFromProblem(nameOptional, problemId, sampleId, inputText, outputText, isPublicOptional);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
    }

    @Operation( summary = "Delete sample from problem")
    @DeleteMapping("problem/{problemId}/sample/{sampleId}")
    @PreAuthorize("hasAuthority('ROLE_JUDGE')")
    public ResponseEntity<String> deleteSampleFromProblem(@PathVariable String problemId, @PathVariable String sampleId) {
        problemId = Sanitizer.removeLineBreaks(problemId);
        sampleId = Sanitizer.removeLineBreaks(sampleId);

        String salida = problemService.deleteSampleFromProblem(problemId, sampleId);
        if (salida.equals("OK")) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(salida, HttpStatus.NOT_FOUND);
    }
}
