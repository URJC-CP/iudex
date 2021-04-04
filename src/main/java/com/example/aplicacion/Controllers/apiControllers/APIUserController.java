package com.example.aplicacion.Controllers.apiControllers;

import com.example.aplicacion.Pojos.UserAPI;
import com.example.aplicacion.Pojos.UserString;
import com.example.aplicacion.services.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/API/v1")
public class APIUserController {
	@Autowired
	UserService userService;

	@ApiOperation("Creates User")
	@PostMapping("/user")
	public ResponseEntity<UserAPI> createUser(@RequestParam String nickname, @RequestParam String email) {
		UserString salida = userService.crearUsuario(nickname, email);
		if (salida.getSalida().equals("OK")) {
			return new ResponseEntity<>(salida.getUser().toUserAPI(), HttpStatus.OK);
		} else {
			return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation("Delete User")
	@DeleteMapping("/user/{userId}")
	public ResponseEntity deleteUser(@PathVariable String userId, @RequestParam String nickname) {
		String salida = userService.deleteUserByNickname(nickname);
		if (salida.equals("OK")) {
			return new ResponseEntity<>(HttpStatus.OK);
		} else {
			return new ResponseEntity(salida, HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation("Update User")
	@PutMapping("/user/{userId}")
	public ResponseEntity<UserAPI> updateUser(@PathVariable String userId, @RequestParam(required = false) Optional<String> nickname, @RequestParam(required = false) Optional<String> email) {
		UserString salida = userService.updateUser(userId, nickname, email);
		if (salida.getSalida().equals("OK")) {
			return new ResponseEntity<>(salida.getUser().toUserAPI(), HttpStatus.OK);
		} else {
			return new ResponseEntity(salida.getSalida(), HttpStatus.NOT_FOUND);
		}
	}
}
