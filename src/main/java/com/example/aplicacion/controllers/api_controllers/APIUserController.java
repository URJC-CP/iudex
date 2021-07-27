package com.example.aplicacion.controllers.api_controllers;

import com.example.aplicacion.pojos.UserAPI;
import com.example.aplicacion.pojos.UserString;
import com.example.aplicacion.services.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.aplicacion.utils.Sanitizer.sanitize;

@RestController
@CrossOrigin(methods = {RequestMethod.POST})
public class APIUserController {
    @Autowired
    UserService userService;

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

