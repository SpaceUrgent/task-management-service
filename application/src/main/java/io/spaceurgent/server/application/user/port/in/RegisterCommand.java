package io.spaceurgent.server.application.user.port.in;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Data
public class RegisterCommand {
    @Email(message = "Invalid email")
    private String email;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @ToString.Exclude
    @Size(min = 6)
    private byte[] password;
}
