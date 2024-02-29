package es.urjc.etsii.grafo.iudex.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final UserDetailsServiceImp userDetailsService;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtRequestFilter(UserDetailsServiceImp userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // If there is not an Authorization header, we skip the filter
        try {
            var claims = jwtTokenProvider.validateToken(request);
            var userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
            var authentication = new UsernamePasswordAuthenticationToken(
					userDetails,
					null,
					userDetails.getAuthorities()
			);

			// TODO Review: No entiendo para que sirve copiar los details, eliminado por ahora
            // authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
			// if there has been any kind of error, make sure authentication is empty
			// so access to protected resources will fail
			SecurityContextHolder.clearContext();
            log.info("Exception processing JWT Token", ex);
        }

        filterChain.doFilter(request, response);
    }

}
