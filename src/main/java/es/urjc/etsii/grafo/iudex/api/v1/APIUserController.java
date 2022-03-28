package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.pojos.UserAPI;
import es.urjc.etsii.grafo.iudex.pojos.UserString;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static es.urjc.etsii.grafo.iudex.utils.Sanitizer.sanitize;

@RestController
@CrossOrigin(methods = {RequestMethod.POST})
public class APIUserController {
    @Autowired
    UserAndTeamService userService;

    @ApiOperation("Creates a User")
    @PostMapping("/API/v1/user")
    public ResponseEntity<UserAPI> createUser(@RequestParam String username, @RequestParam String email) {
        username = sanitize(username);
        email = sanitize(email);

        UserString salida = userService.crearUsuario(username, email);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getUser().toUserAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

