package com.task.managment.web.controller;

import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.port.in.dto.RegisterUserDto;
import com.task.managment.web.dto.ErrorDto;
import com.task.managment.web.dto.UserDto;
import com.task.managment.web.mapper.WebUserMapper;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final WebUserMapper userMapper;
    private final RegisterUserUseCase registerUserUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDto register(@Valid RegisterUserDto registerUserDto) throws EmailExistsException {
        final var registered = registerUserUseCase.register(registerUserDto);
        return userMapper.toDto(registered);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailExistsException.class)
    public ErrorDto handleEmailExistsException(EmailExistsException exception, HttpServletRequest request) {
        return ErrorDto.builder()
                .reason("Bad request")
                .message(exception.getMessage())
                .request(request)
                .build();
    }
}
