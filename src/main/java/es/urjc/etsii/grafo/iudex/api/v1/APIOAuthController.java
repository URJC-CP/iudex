package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.exceptions.IudexException;
import es.urjc.etsii.grafo.iudex.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@CrossOrigin(methods = { RequestMethod.GET })
@RequestMapping("/API/v1/oauth")
public class APIOAuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public ResponseEntity<User> login(@AuthenticationPrincipal OidcUser oidcUser) {
        try {
            User user = userService.loginUserFromOAuthPrincipal(oidcUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IudexException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/signup")
    public ResponseEntity<User> getSession(@AuthenticationPrincipal OidcUser oidcUser) {
        try {
            User user = userService.signupUserFromOAuthPrincipal(oidcUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IudexException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
