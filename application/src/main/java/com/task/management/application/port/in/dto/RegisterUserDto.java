package com.task.management.application.port.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class RegisterUserDto {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @ToString.Exclude
    @Size(min = 8, message = "Password must contain at least 8 characters")
    private char[] password;
}
