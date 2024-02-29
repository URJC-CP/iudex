package es.urjc.etsii.grafo.iudex.security;

import java.time.Duration;
import java.time.LocalDateTime;

public class Token {

	private TokenType tokenType;
	private String tokenValue;
	private LocalDateTime expiryDate;

	public Token(TokenType tokenType, String tokenValue, LocalDateTime expiryDate) {
		super();
		this.tokenType = tokenType;
		this.tokenValue = tokenValue;
		this.expiryDate = expiryDate;
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}
}
