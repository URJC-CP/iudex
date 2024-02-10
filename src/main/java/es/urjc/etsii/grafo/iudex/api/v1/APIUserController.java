package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.pojos.UserAPI;
import es.urjc.etsii.grafo.iudex.repositories.UserRepository;
import es.urjc.etsii.grafo.iudex.services.UserAndTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class APIUserController {

    private static final Logger log = LoggerFactory.getLogger(APIUserController.class);

    final UserAndTeamService userAndTeamService;
    final UserRepository userRepository;

    public APIUserController(UserAndTeamService userAndTeamService, UserRepository userRepository) {
        this.userAndTeamService = userAndTeamService;
        this.userRepository = userRepository;
    }

//
    @GetMapping("/API/v1/user/me")
    @Operation( summary = "Get current user identifier by the current token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current authenticated user"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token provided")
    })
    public ResponseEntity<UserAPI> getCurrentUser(Authentication authentication){
        if(!authentication.isAuthenticated()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String nickname = authentication.getName(); // Our User::nickname is used as the Spring User::username
        if(nickname == null || nickname.isBlank()){
            log.warn("Invalid username extracted from auth token: " + (nickname));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var user = userRepository.findByNickname(nickname);
        if(user.isEmpty()){
            // May happen but should be extremely rare, log it
            log.warn("User %s has a signed token but the username does not exist?".formatted(nickname));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user.get().toUserAPI());
    }

    @Operation( summary = "Add a specific role to an existing user")
    @PostMapping("/API/v1/user/{id}/role/{role}")
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

