package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.security.AuthResponse;
import es.urjc.etsii.grafo.iudex.security.JwtTokenProvider;
import es.urjc.etsii.grafo.iudex.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtTokenProvider tokenProvider;

    public APIOAuthController(UserService userService, JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(HttpServletRequest req){
        // The only endpoint that receives a refresh token in the auth header instead of an access token
        var accessToken = tokenProvider.refreshToken(req);
        return ResponseEntity.ok(new AuthResponse(
                AuthResponse.Status.REFRESHED,
                "Generated a new Access Token",
                accessToken,
                ""
        ));
    }

    @GetMapping("/exchange")
    public ResponseEntity<AuthResponse> login3(@RequestParam("token") String uid){
        var authObject = userService.completeLogin(uid);
        if(authObject == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authObject);
    }

    // Starts login
    @GetMapping("/login")
    public void login1(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/keycloak");
    }

    // Gets result form auth provider
    @GetMapping("/completeLogin")
    public void login2(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
        if(oAuth2User == null) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid login state");
            return;
        }

        String uid = userService.prepareForLogin(oAuth2User);
        response.sendRedirect("/?loginid=" + uid);
    }
}
