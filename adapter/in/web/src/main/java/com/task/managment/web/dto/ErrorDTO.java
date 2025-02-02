package com.task.managment.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDTO {
    private final Instant timestamp;
    private final String reason;
    private final String message;
    private final Map<String, String> errors;
    private final String path;

    @Builder
    public ErrorDTO(String reason,
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
