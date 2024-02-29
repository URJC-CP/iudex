package es.urjc.etsii.grafo.iudex.security;

public class AuthResponse {

    private Status status;
    private String message;
    private String error;

    private String accessToken;

    private String refreshToken;

    public enum Status {
        SUCCESS, REFRESHED, FAILURE
    }

    public AuthResponse() {
    }

    public AuthResponse(Status status, String message, String accessToken, String refreshToken) {
        this.status = status;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthResponse(Status status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
