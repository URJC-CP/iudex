package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class APIUserController {

    @Autowired
    UserAndTeamService userAndTeamService;

    @Operation( summary = "Add a specific role to an existing user")
    @PostMapping("/API/v1/user/{id}/role/{role}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<String>> addRoleToUser(@PathVariable long id, @PathVariable String role) {
        Optional<User> optionalUser = userAndTeamService.getUserById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        role = "ROLE_" + role.toUpperCase();
        if (!List.of("ROLE_ADMIN", "ROLE_JUDGE", "ROLE_USER").contains(role)) return ResponseEntity.badRequest().build();

        User user = optionalUser.get();
        if (user.getRoles().contains(role)) return ResponseEntity.badRequest().build();
        else user = userAndTeamService.addRoleToUser(role, user);

        return ResponseEntity.ok(user.getRoles());
    }

    @Operation( summary = "Remove a role from an existing user")
    @DeleteMapping("/API/v1/user/{id}/role/{role}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<String>> removeRoleFromUser(@PathVariable long id, @PathVariable String role) {
        Optional<User> optionalUser = userAndTeamService.getUserById(id);
        if (optionalUser.isEmpty()) return ResponseEntity.notFound().build();

        role = "ROLE_" + role.toUpperCase();
        if (!List.of("ROLE_ADMIN", "ROLE_JUDGE", "ROLE_USER").contains(role)) return ResponseEntity.badRequest().build();

        User user = optionalUser.get();
        if (!user.getRoles().contains(role)) return ResponseEntity.notFound().build();
        else user = userAndTeamService.removeRoleFromUser(role, user);

        return ResponseEntity.ok(user.getRoles());
    }

}

