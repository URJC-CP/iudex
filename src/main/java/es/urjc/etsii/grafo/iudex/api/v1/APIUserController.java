package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.pojos.UserAPI;
import es.urjc.etsii.grafo.iudex.pojos.UserString;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import es.urjc.etsii.grafo.iudex.utils.Sanitizer;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(methods = {RequestMethod.POST})
public class APIUserController {

    @Autowired
    UserAndTeamService userAndTeamService;

    @Operation( summary = "Add a specific role to an existing user")
    @PostMapping("/API/v1/user/{id}/role")
    public ResponseEntity<UserAPI> createUser(@RequestParam String username, @RequestParam String email) {
    public ResponseEntity<List<String>> addRoleToUser(@PathVariable long id, @RequestParam String role) {
        Optional<User> optionalUser = userAndTeamService.getUserById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        role = "ROLE_" + role.toUpperCase();
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_JUDGE") && !role.equals("ROLE_USER")) return ResponseEntity.badRequest().build();

        User user = optionalUser.get();
        if (!user.getRoles().contains(role)) user.getRoles().add(role);

        return ResponseEntity.ok(user.getRoles());
    }

    @Operation( summary = "Remove a role from an existing user")
    @PostMapping("/API/v1/user/{id}/role/{role}")
    public ResponseEntity<List<String>> removeRoleToUser(@PathVariable long id, @PathVariable String role) {
        Optional<User> optionalUser = userAndTeamService.getUserById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        role = "ROLE_" + role.toUpperCase();
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_JUDGE") && !role.equals("ROLE_USER")) return ResponseEntity.badRequest().build();

        User user = optionalUser.get();
        if (!user.getRoles().contains(role)) return ResponseEntity.badRequest().build();
        else user.getRoles().remove(role);

        return ResponseEntity.ok(user.getRoles());
    }

}

