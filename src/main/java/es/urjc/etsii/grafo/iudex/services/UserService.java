package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.exceptions.IudexException;
import es.urjc.etsii.grafo.iudex.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getUserFromOAuthPrincipal(OidcUser oAuth2User) {
        Optional<User> user = userRepository.findUserByEmail(oAuth2User.getAttribute("email"));

        if (user.isEmpty()) throw new IudexException("User not registered");

        return user.get();
    }

    public User signupUserFromOAuthPrincipal(OidcUser oAuth2User) {
        User user = new User(
                oAuth2User.getAttribute("preferred_username").toString(),
                oAuth2User.getAttribute("email").toString(),
                oAuth2User.getAttribute("given_name").toString(),
                oAuth2User.getAttribute("family_name").toString()
        );

        if (userRepository.existsUserByEmail(user.getEmail()) || userRepository.existsUserByNickname(user.getNickname())) {
            throw new IudexException("User already exists");
        }

        userRepository.save(user);

        return user;
    }

}
