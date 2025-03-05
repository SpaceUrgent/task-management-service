package com.task.managment.web.controller;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.iam.port.in.GetUserProfileUseCase;
import com.task.managment.web.dto.UserProfileDto;
import com.task.managment.web.mapper.UserProfileResponseMapper;
import com.task.managment.web.security.SecuredUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UserProfileResponseMapper userProfileResponseMapper;

    @GetMapping(
            value = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserProfileDto getUserProfile() throws UseCaseException {
        final var userProfile = getUserProfileUseCase.getUserProfile(currentUser().getId());
        return userProfileResponseMapper.toResponse(userProfile);
    }

    private SecuredUser currentUser() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
