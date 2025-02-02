package com.task.managment.web.controller;

import com.task.management.application.dto.UserDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.port.in.GetUserByEmailUseCase;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.managment.web.security.SecuredUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserUseCase getUserUseCase;
    private final GetUserByEmailUseCase getUserByEmailUseCase;

    @GetMapping("/email/{email}")
    public UserDTO getUserByEmail(@PathVariable String email) throws EntityNotFoundException {
        return getUserByEmailUseCase.getUser(email);
    }

    @GetMapping(
            value = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserDTO getUserProfile() throws EntityNotFoundException {
        return getUserUseCase.getUser(currentUser().getId());
    }

    private SecuredUser currentUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
