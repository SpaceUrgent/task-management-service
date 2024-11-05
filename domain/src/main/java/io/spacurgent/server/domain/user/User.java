package io.spacurgent.server.domain.user;

import lombok.Builder;

import java.util.Set;

public class User {
    private UserId id;
    private String email;
    private String firstName;
    private String lastName;
    private String hashedPassword;
    private Set<Role> roles;

    @Builder
    User(String email, String firstName, String lastName, String hashedPassword, Set<Role> roles) {
        assert email != null : "Email name is required";
        assert firstName != null : "First name is required";
        assert lastName != null : "Last name is required";
        assert hashedPassword != null : "Hashed password is required";
        assert roles != null && !roles.isEmpty() : "Roles set is required";
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hashedPassword = hashedPassword;
        this.roles = roles;
    }
}
