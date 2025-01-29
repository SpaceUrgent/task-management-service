package com.task.managment.web.mapper;

import com.task.management.application.model.User;
import com.task.managment.web.dto.UserDto;

import java.util.List;

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

    public List<UserDto> toDtoList(List<User> members) {
        requireNonNull(members, "Member list is required");
        return members.stream().map(this::toDto).toList();
    }
}
