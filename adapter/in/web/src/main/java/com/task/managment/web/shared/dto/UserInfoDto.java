package com.task.managment.web.shared.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserInfoDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;

    @Builder
    public UserInfoDto(Long id,
                       String email,
                       String firstName,
                       String lastName,
                       String fullName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
    }
}
