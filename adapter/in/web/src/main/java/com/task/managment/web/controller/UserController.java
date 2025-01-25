package com.task.managment.web.controller;

import com.task.management.application.exception.UserNotFoundException;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.managment.web.dto.ErrorDto;
import com.task.managment.web.dto.UserDto;
import com.task.managment.web.mapper.WebUserMapper;
import com.task.managment.web.security.SecuredUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final WebUserMapper userMapper;

    @GetMapping(
            value = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDto getUserProfile() {
        final var user = getUserUseCase.getUser(currentUser().getId());
        return userMapper.toDto(user);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDto handleUserNotFoundException(UserNotFoundException exception,
                                                HttpServletRequest request) {
        return ErrorDto.builder()
                .reason("Internal error")
                .message(exception.getMessage())
                .request(request)
                .build();
    }

    private SecuredUser currentUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
