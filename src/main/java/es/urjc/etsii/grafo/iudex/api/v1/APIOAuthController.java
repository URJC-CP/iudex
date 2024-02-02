package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.security.jwt.AuthResponse;
import es.urjc.etsii.grafo.iudex.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/API/v1/oauth")
public class APIOAuthController {

    private final UserService userService;

    public APIOAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/exchange")
    public ResponseEntity<AuthResponse> login2(@RequestParam("uid") String uid){
        var authObject = userService.completeLogin(uid);
        if(authObject == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authObject);
    }

    @GetMapping("/login")
    // @PreAuthorize("hasAuthority('OIDC_USER')")
    public void login1(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
        String uid = userService.prepareForLogin(oAuth2User);
        response.sendRedirect("/?loginid=" + uid);
    }
}
