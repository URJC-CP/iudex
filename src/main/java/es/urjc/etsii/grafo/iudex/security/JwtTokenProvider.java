package es.urjc.etsii.grafo.iudex.security;

import es.urjc.etsii.grafo.iudex.exceptions.JwtIudexException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JwtTokenProvider {
	private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
	private final SecretKey jwtSecret = Jwts.SIG.HS256.key().build();
	private final JwtParser jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

	// username --> refreshToken
	private final Map<String, String> refreshTokens = new ConcurrentHashMap<>();
	private final Set<String> validTokens = ConcurrentHashMap.newKeySet();
	private final UserDetailsServiceImp userDetailsServiceImp;

    public JwtTokenProvider(UserDetailsServiceImp userDetailsServiceImp) {
        this.userDetailsServiceImp = userDetailsServiceImp;
    }

    // TODO use token invalidation, can be implemented later
	public void invalidateRefreshForUser(String username){
		var token = refreshTokens.remove(username);
		if(token == null){
			log.warn("Tried to invalidate refresh token for user %s, but could not find it".formatted(username));
		} else {
			validTokens.remove(token);
		}
	}

	public String refreshToken(HttpServletRequest req){
		var tokenString = tokenStringFromHeaders(req);
		var refreshClaims = validateToken(tokenString);
		if(!refreshClaims.get("type", String.class).equals(TokenType.REFRESH.name())){
			throw new IllegalArgumentException("Cannot refresh token if token type is not REFRESH");
		}
		if(!validTokens.contains(tokenString)){
			throw new IllegalArgumentException("The given refresh token has been manually expired");
		}
		UserDetails userDetails = userDetailsServiceImp.loadUserByUsername(refreshClaims.getSubject());
		return generateAccessToken(userDetails);
	}

	public String tokenStringFromHeaders(HttpServletRequest req){
		String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (bearerToken == null) {
			throw new JwtIudexException("Missing Authorization header");
		}
		if(!bearerToken.startsWith("Bearer ")){
			throw new JwtIudexException("Authorization header does not start with Bearer: " + bearerToken);
		}
		return bearerToken.substring(7);
	}

	public Claims validateToken(HttpServletRequest req){
		return validateToken(tokenStringFromHeaders(req));
	}
	public Claims validateToken(String token) {
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	public String generateAccessToken(UserDetails userDetails) {
		return buildToken(TokenType.ACCESS, userDetails).compact();
	}

	public String generateRefreshToken(UserDetails userDetails) {
		var token = buildToken(TokenType.REFRESH, userDetails);
		var serializedToken = token.compact();
		this.refreshTokens.put(userDetails.getUsername(), serializedToken);
		this.validTokens.add(serializedToken);
		return serializedToken;
	}

	private JwtBuilder buildToken(TokenType tokenType, UserDetails userDetails) {
		var currentDate = new Date();
		var expiryDate = Date.from(new Date().toInstant().plus(tokenType.duration));
		return Jwts.builder()
				.claim("roles", userDetails.getAuthorities())
				.claim("type", tokenType.name())
				.subject(userDetails.getUsername())
				.issuedAt(currentDate)
				.expiration(expiryDate)
				.signWith(jwtSecret);
	}
}
