package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.Result;
import es.urjc.etsii.grafo.iudex.services.ResultService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static es.urjc.etsii.grafo.iudex.utils.Sanitizer.sanitize;

@RestController
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class APIAdminController {

    @Autowired
    ResultService resultService;

    @ApiOperation("Get a full Result")
    @GetMapping("/API/v1/result/{resultId}")
    public ResponseEntity<Result> getResult(@PathVariable String resultId) {
        resultId = sanitize(resultId);

        Result result = resultService.getResult(resultId);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ApiOperation("Get all Results")
    @GetMapping("/API/v1/result/")
    public ResponseEntity<List<Result>> getAllResult() {
        List<Result> resultList = resultService.getAllResults();
        if (resultList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }

}