package es.urjc.etsii.grafo.iudex.api.v1;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.exceptions.IudexException;
import es.urjc.etsii.grafo.iudex.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(methods = { RequestMethod.GET })
@RequestMapping("/API/v1/oauth")
public class APIOAuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/session")
    public ResponseEntity<User> session(@AuthenticationPrincipal OidcUser oidcUser) {
        try {
            User user = userService.getUserFromOAuthPrincipal(oidcUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (IudexException e) {
            User user = userService.signupUserFromOAuthPrincipal(oidcUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }

}