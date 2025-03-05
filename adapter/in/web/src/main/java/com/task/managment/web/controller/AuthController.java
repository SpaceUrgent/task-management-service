package com.task.managment.web.controller;

import com.task.management.domain.iam.exception.EmailExistsException;
import com.task.management.domain.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.iam.port.in.command.RegisterUserCommand;
import com.task.managment.web.dto.response.ErrorResponse;
import com.task.managment.web.dto.request.RegisterUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void register(@Valid RegisterUserRequest request) throws EmailExistsException {
        final var command = RegisterUserCommand.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .build();
        registerUserUseCase.register(command);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailExistsException.class)
    public ErrorResponse handleEmailExistsException(EmailExistsException exception, HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason("Bad request")
                .message(exception.getMessage())
                .request(request)
                .build();
    }
}
