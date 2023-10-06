package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.services.ResultService;
import es.urjc.etsii.grafo.iudex.utils.Sanitizer;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APIAdminController {

    @Autowired
    ResultService resultService;

    @Operation( summary = "Get a full Result")
    @GetMapping("/API/v1/result/{resultId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Result> getResult(@PathVariable String resultId) {
        resultId = Sanitizer.removeLineBreaks(resultId);

        Result result = resultService.getResult(resultId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation( summary = "Get all Results")
    @GetMapping("/API/v1/result/")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Result>> getAllResult() {
        List<Result> resultList = resultService.getAllResults();
        if (resultList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

}