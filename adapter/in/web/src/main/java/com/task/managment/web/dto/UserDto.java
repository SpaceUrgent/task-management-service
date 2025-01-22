package com.task.managment.web.dto;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.requireNonNull;

@Data
public class UserDto {
    private final Long id;
    private final String email;
    private final String firstName;
    private final String lastName;

    @Builder
    public UserDto(Long id,
                   String email,
                   String firstName,
                   String lastName) {
        this.id = requireNonNull(id, "User id is required");
        this.email = requireNonNull(email, "Email is required");
        this.firstName = requireNonNull(firstName, "First name is required");
        this.lastName = requireNonNull(lastName, "Last name is required");
    }
}
