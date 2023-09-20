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

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        return user.orElse(null);

    }

    public User signupUserFromOAuthPrincipal(OidcUser oAuth2User) {
        User user;

        try {
            user = new User(
                    Objects.requireNonNull(oAuth2User.getAttribute("preferred_username")).toString(),
                    Objects.requireNonNull(oAuth2User.getAttribute("email")).toString(),
                    Objects.requireNonNull(oAuth2User.getAttribute("given_name")).toString(),
                    Objects.requireNonNull(oAuth2User.getAttribute("family_name")).toString()
            );
        } catch (NullPointerException e) {
            throw new IudexException("OAuth2 account has required values missing");
        }

        List<String> roles = new ArrayList<>();
        oAuth2User.getAuthorities().forEach(authority -> roles.add(authority.getAuthority()));
        user.setRoles(roles);

        if (userRepository.existsUserByEmail(user.getEmail()) || userRepository.existsUserByNickname(user.getNickname())) {
            throw new IudexException("User already exists");
        }

        userRepository.save(user);

        return user;
    }

    public AuthResponse loginUser(OidcUser oidcUser) {
        User user = getUserFromOAuthPrincipal(oidcUser);

        if (user == null) {
            try {
                user = signupUserFromOAuthPrincipal(oidcUser);
            } catch (IudexException e) {
                return new AuthResponse(
                        AuthResponse.Status.FAILURE,
                        "Iudex account not found and an error has occurred while trying to sign up",
                        e.getMessage()
                );
            }
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
