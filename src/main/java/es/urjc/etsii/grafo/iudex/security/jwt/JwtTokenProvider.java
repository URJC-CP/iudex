package es.urjc.etsii.grafo.iudex.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtTokenProvider {
	
	private static final Logger LOG = LoggerFactory.getLogger(JwtTokenProvider.class);

	private static final long JWT_EXPIRATION_IN_MS = 5400000;

	private static final Long REFRESH_TOKEN_EXPIRATION_MSEC = 10800000L;

	public String getUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(Keys.secretKeyFor(SignatureAlgorithm.HS256)).build()
				.parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(Keys.secretKeyFor(SignatureAlgorithm.HS256)).build()
					.parseClaimsJws(token);
			return true;
		} catch (JwtException ex) {
			LOG.debug("Invalid JWT Token");
		} catch (IllegalArgumentException ex) {
			LOG.debug("JWT claims string is empty");
		}
		return false;
	}

	public Token generateAccessToken(UserDetails userDetails) {
		return generateToken(Token.TokenType.ACCESS, userDetails);

	}

	public Token generateRefreshToken(UserDetails userDetails) {
		return generateToken(Token.TokenType.REFRESH, userDetails);
	}

	private Token generateToken(Token.TokenType tokenType, UserDetails userDetails) {
		Claims claims = Jwts.claims().setSubject(userDetails.getUsername());

		claims.put("roles", userDetails.getAuthorities());

		long time = (tokenType == Token.TokenType.ACCESS) ? JWT_EXPIRATION_IN_MS : REFRESH_TOKEN_EXPIRATION_MSEC;
		Date now = new Date();
		Long duration = now.getTime() + time;
		Date expiryDate = new Date(now.getTime() + time);
		String token = Jwts.builder().setClaims(claims).setSubject((userDetails.getUsername())).setIssuedAt(new Date())
				.setExpiration(expiryDate).signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256)).compact();

		return new Token(tokenType, token, duration,
				LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
	}
}
