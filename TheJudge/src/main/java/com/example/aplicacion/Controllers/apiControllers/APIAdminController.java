package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Entities.Result;
import com.example.aplicacion.Entities.Submission;
import com.example.aplicacion.Pojos.SubmissionAPI;
import com.example.aplicacion.services.ResultService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class APIAdminController {
    @Autowired
    ResultService resultService;

    @ApiOperation("Get a full Result")
    @GetMapping("/API/v1/result/{resultId}")
    public ResponseEntity<Result> getResult(@PathVariable String resultId){
        Result result = resultService.getResult(resultId);
        if (result == null){
            return  new ResponseEntity("RESULT NOT FOUND", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

}
