package es.urjc.etsii.grafo.iudex.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static es.urjc.etsii.grafo.iudex.security.JwtRequestFilter.log;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled=true)
class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        // Permissions are managed by each controller method
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        // We do not need cookies or CSRF or CORS in a REST API
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Disable CORS and CSRF protection too
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable);

        // Override default Spring redirection behaviour on auth failure
        http.exceptionHandling(eh -> eh.authenticationEntryPoint((req, rep, e) -> {
            log.info("Failed auth request", e);
            rep.sendError(HttpStatus.UNAUTHORIZED.value());
        }));

        // Configure oauth and our filter
        http.oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/API/v1/oauth/completeLogin", true));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}