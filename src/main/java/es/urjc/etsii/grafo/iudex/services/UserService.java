package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.exceptions.IudexException;
import es.urjc.etsii.grafo.iudex.repositories.UserRepository;
import es.urjc.etsii.grafo.iudex.security.jwt.AuthResponse;
import es.urjc.etsii.grafo.iudex.security.jwt.JwtTokenProvider;
import es.urjc.etsii.grafo.iudex.security.jwt.UserDetailsServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsServiceImp userDetailsServiceImp;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    public AuthResponse loginUser(OidcUser oidcUser) {
        User user;
        try {
            user = getUserFromOAuthPrincipal(oidcUser);
        } catch (IudexException e) {
            user = signupUserFromOAuthPrincipal(oidcUser);
        }

        UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(user.getNickname());

        return new AuthResponse(
                AuthResponse.Status.SUCCESS,
                "Successfully logged in",
                jwtTokenProvider.generateAccessToken(userDetails),
                jwtTokenProvider.generateRefreshToken(userDetails)
        );
    }

}
