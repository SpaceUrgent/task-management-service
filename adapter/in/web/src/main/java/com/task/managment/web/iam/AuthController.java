package com.task.managment.web.iam;

import com.task.managment.web.shared.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void login(LoginRequest loginRequest,
                      HttpServletRequest servletRequest,
                      HttpServletResponse servletResponse) {
        var token = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.getEmail(), loginRequest.getPassword());
        var authentication = authenticationManager.authenticate(token);
        var context = securityContextHolderStrategy.createEmptyContext();

        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, servletRequest, servletResponse);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public final ErrorResponse handleAuthenticationException(AuthenticationException exception,
                                                             HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason(ErrorResponse.REASON_BAD_REQUEST)
                .message("Incorrect email or password")
                .request(request)
                .build();
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}
