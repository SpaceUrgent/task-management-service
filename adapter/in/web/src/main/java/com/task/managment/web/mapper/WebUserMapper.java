package com.task.managment.web.mapper;

import com.task.management.application.model.User;
import com.task.managment.web.dto.UserDto;

import static java.util.Objects.requireNonNull;

public class WebUserMapper {

    public UserDto toDto(User user) {
        requireNonNull(user, "User is required");
        return UserDto.builder()
                .id(user.getId().value())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
