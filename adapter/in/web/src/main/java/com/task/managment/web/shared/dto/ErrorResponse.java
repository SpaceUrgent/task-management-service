package com.task.managment.web.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    public final static String REASON_UNAUTHORIZED = "Unauthorized";
    public final static String REASON_BAD_REQUEST = "Bad request";
    public final static String REASON_ENTITY_NOT_FOUND = "Entity not found";
    public final static String REASON_ACTION_NOT_ALLOWED = "Action not allowed";
    public final static String MESSAGE_UNAUTHORIZED = "Authorization required";
    public final static String MESSAGE_INVALID_CREDENTIALS = "Invalid username or password";
    public final static String MESSAGE_INVALID_REQUEST = "Request validation error";
    public final static String MESSAGE_MISSING_REQUEST_BODY = "Request body is missing";

    private final Instant timestamp;
    private final String reason;
    private final String message;
    private final Map<String, String> errors;
    private final String path;

    @Builder
    public ErrorResponse(String reason,
                         String message,
                         Map<String, String> errors,
                         HttpServletRequest request) {
        this.timestamp = Instant.now();
        this.reason = reason;
        this.message = message;
        this.errors = errors;
        this.path = ServletUriComponentsBuilder.fromRequest(request).build().getPath();
    }
}
