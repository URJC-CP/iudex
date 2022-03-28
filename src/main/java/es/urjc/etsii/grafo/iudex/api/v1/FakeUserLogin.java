package es.urjc.etsii.grafo.iudex.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/API/v1/")
@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class FakeUserLogin {

    @Autowired
    private ResourceLoader resourceLoader;


    //Get all problems in DB
    @ApiOperation("Fake login, pending the real thing implementation")
    @GetMapping("/login/{user}")
    public ResponseEntity<Object> problems(@PathVariable String user) throws IOException {

        // TODO Delete from resources the hardcoded responses once the real login is implemented
        if(!"".equals(user) && user.substring(0,1).equals(user.substring(0,1).toUpperCase())) {
            return new ResponseEntity<>(readResponse("loginResponses/teacher_login.json"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(readResponse("loginResponses/student_login.json"), HttpStatus.OK);
        }
    }

    protected Object readResponse(String jsonFileName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(resourceLoader.getResource("classpath:"+jsonFileName).getInputStream(), Object.class);
    }
}
