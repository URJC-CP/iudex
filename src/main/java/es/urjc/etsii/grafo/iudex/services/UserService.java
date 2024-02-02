package es.urjc.etsii.grafo.iudex.services;

import es.urjc.etsii.grafo.iudex.entities.User;
import es.urjc.etsii.grafo.iudex.exceptions.IudexException;
import es.urjc.etsii.grafo.iudex.repositories.UserRepository;
import es.urjc.etsii.grafo.iudex.security.jwt.AuthResponse;
import es.urjc.etsii.grafo.iudex.security.jwt.JwtTokenProvider;
import es.urjc.etsii.grafo.iudex.security.jwt.UserDetailsServiceImp;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserDetailsServiceImp userDetailsServiceImp;

    private final JwtTokenProvider jwtTokenProvider;

    private final Map<String, AuthResponse> pendingLogins = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository, UserDetailsServiceImp userDetailsServiceImp, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userDetailsServiceImp = userDetailsServiceImp;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User getUserFromOAuthPrincipal(OAuth2User oAuth2User) {
        Optional<User> user = userRepository.findUserByEmail(oAuth2User.getAttribute("email"));

        return user.orElse(null);
    }

    public User signupUserFromOAuthPrincipal(OAuth2User oAuth2User) {
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

        user.setRoles(List.of("ROLE_USER"));

        if (userRepository.existsUserByEmail(user.getEmail()) || userRepository.existsUserByNickname(user.getNickname())) {
            throw new IudexException("User already exists");
        }

        userRepository.save(user);

        return user;
    }

    public String prepareForLogin(OAuth2User oAuth2User) {
        var UUID = java.util.UUID.randomUUID().toString();
        User user = getUserFromOAuthPrincipal(oAuth2User);

        if (user == null) {
            try {
                user = signupUserFromOAuthPrincipal(oAuth2User);
            } catch (IudexException e) {
                this.pendingLogins.put(UUID, new AuthResponse(
                        AuthResponse.Status.FAILURE,
                        "Iudex account not found and an error has occurred while trying to sign up",
                        e.getMessage()
                ));
            }
        }

        UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(user.getNickname());

        var authResponse = new AuthResponse(
                AuthResponse.Status.SUCCESS,
                "Successfully logged in",
                jwtTokenProvider.generateAccessToken(userDetails),
                jwtTokenProvider.generateRefreshToken(userDetails)
        );
        this.pendingLogins.put(UUID, authResponse);
        return UUID;
    }

    public AuthResponse completeLogin(String uid) {
        return this.pendingLogins.remove(uid);
    }
}
