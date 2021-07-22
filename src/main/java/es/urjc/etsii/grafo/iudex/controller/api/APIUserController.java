package es.urjc.etsii.grafo.iudex.controller.api;

import es.urjc.etsii.grafo.iudex.pojo.UserAPI;
import es.urjc.etsii.grafo.iudex.pojo.UserString;
import es.urjc.etsii.grafo.iudex.service.UserService;
import es.urjc.etsii.grafo.iudex.util.Sanitizer;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(methods = {RequestMethod.POST})
public class APIUserController {
    @Autowired
    UserService userService;

    @ApiOperation("Creates a User")
    @PostMapping("/API/v1/user")
    public ResponseEntity<UserAPI> createUser(@RequestParam String username, @RequestParam String email) {
        username = Sanitizer.sanitize(username);
        email = Sanitizer.sanitize(email);

        UserString salida = userService.crearUsuario(username, email);
        if (salida.getSalida().equals("OK")) {
            return new ResponseEntity<>(salida.getUser().toUserAPI(), HttpStatus.OK);
        } else {
            return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
        }
    }

}

