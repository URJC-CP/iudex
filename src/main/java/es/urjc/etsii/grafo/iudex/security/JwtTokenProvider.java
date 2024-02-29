package es.urjc.etsii.grafo.iudex.security;

import es.urjc.etsii.grafo.iudex.exceptions.JwtIudexException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

@Component
public class JwtTokenProvider {
	
	private static final ZoneId zone = ZoneOffset.systemDefault();
	private final SecretKey jwtSecret = Jwts.SIG.HS256.key().build();
	private final JwtParser jwtParser = Jwts.parser().verifyWith(jwtSecret).build();

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public Claims validateToken(HttpServletRequest req){
		String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (bearerToken == null) {
			throw new JwtIudexException("Missing Authorization header");
		}
		if(!bearerToken.startsWith("Bearer ")){
			throw new JwtIudexException("Authorization header does not start with Bearer: " + bearerToken);
		}

		return validateToken(bearerToken.substring(7));
	}
	public Claims validateToken(String token) {
		return jwtParser.parseSignedClaims(token).getPayload();
	}

	public Token generateAccessToken(UserDetails userDetails) {
		return generateToken(TokenType.ACCESS, userDetails);
	}

	public Token generateRefreshToken(UserDetails userDetails) {
		return generateToken(TokenType.REFRESH, userDetails);
	}

	private Token generateToken(TokenType tokenType, UserDetails userDetails) {
		var currentDate = new Date();
		var expiryDate = Date.from(new Date().toInstant().plus(tokenType.duration));
		String token = Jwts.builder()
				.claim("roles", userDetails.getAuthorities())
				.subject(userDetails.getUsername())
				.issuedAt(currentDate)
				.expiration(expiryDate)
				.signWith(jwtSecret)
				.compact();

		return new Token(tokenType, token, LocalDateTime.ofInstant(expiryDate.toInstant(), zone));
	}
}
