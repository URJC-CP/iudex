package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.security.jwt.AuthResponse;
import es.urjc.etsii.grafo.iudex.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/API/v1/oauth")
public class APIOAuthController {

    private final UserService userService;

    public APIOAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    @PreAuthorize("hasAuthority('OIDC_USER')")
    public ResponseEntity<AuthResponse> session(@AuthenticationPrincipal OAuth2User oAuth2User) {
        AuthResponse authResponse = userService.loginUser(oAuth2User);

        if (authResponse.getError() == null) return new ResponseEntity<>(authResponse, HttpStatus.OK);
        else return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
    }

}
