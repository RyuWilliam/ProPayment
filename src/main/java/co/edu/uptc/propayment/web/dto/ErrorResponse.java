package co.edu.uptc.propayment.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class ErrorResponse {

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("error_message")
    private String errorMessage;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("path")
    private String path;

    @JsonProperty("status")
    private int status;

    public ErrorResponse(String errorCode, String errorMessage, int status) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String errorCode, String errorMessage, int status, String path) {
        this(errorCode, errorMessage, status);
        this.path = path;
    }

    // Getters y Setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}