package com.task.management.application.service.mapper;

import com.task.management.application.dto.UserDTO;
import com.task.management.application.model.User;

public class UserMapper {

    public UserDTO toDTO(User model) {
        return UserDTO.builder()
                .id(model.getId().value())
                .email(model.getEmail())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .build();
    }
}
