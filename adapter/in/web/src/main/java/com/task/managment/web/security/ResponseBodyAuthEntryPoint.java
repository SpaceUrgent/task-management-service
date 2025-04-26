package com.task.managment.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.managment.web.common.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class ResponseBodyAuthEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final var errorResponse = errorResponse(request);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private static ErrorResponse errorResponse(HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason(ErrorResponse.REASON_UNAUTHORIZED)
                .message(ErrorResponse.MESSAGE_UNAUTHORIZED)
                .request(request)
                .build();
    }
}
