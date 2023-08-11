package es.urjc.etsii.grafo.iudex.security.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
	
	private static final Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);

	private final String jwtSecret = getJwtSecret();

	private static final long JWT_EXPIRATION_IN_MS = 5400000;

	private static final Long REFRESH_TOKEN_EXPIRATION_MSEC = 10800000L;

	private static String getJwtSecret() {
		try {
			File jwtSecretFile = new File("jwt.secret");

			if (jwtSecretFile.createNewFile()){
				LOG.debug("JWT Secret file created successfully");
			}

			return Files.readString(jwtSecretFile.toPath());
		} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
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
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (SignatureException ex) {
			LOG.debug("Invalid JWT Signature");
		} catch (MalformedJwtException ex) {
			LOG.debug("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			LOG.debug("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			LOG.debug("Unsupported JWT exception");
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

		claims.put("auth", userDetails.getAuthorities().stream().map(s -> new SimpleGrantedAuthority("ROLE_"+s)).collect(Collectors.toList()));
		Date now = new Date();
		Long duration = now.getTime() + REFRESH_TOKEN_EXPIRATION_MSEC;
		Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MSEC);
		String token = Jwts.builder().setClaims(claims).setSubject((userDetails.getUsername())).setIssuedAt(new Date())
				.setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, jwtSecret).compact();

		return new Token(tokenType, token, duration,
				LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
	}
}