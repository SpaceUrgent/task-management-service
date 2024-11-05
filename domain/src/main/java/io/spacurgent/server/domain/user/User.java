package io.spacurgent.server.domain.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode
@ToString
public class User {
    @Getter
    private final UserId id;
    @Getter
    private final String email;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @ToString.Exclude
    private String hashedPassword;
    private final Set<Role> roles;

    @Builder
    User(String email, String firstName, String lastName, String hashedPassword, Set<Role> roles) {
        assert email != null : "Email name is required";
        assert firstName != null : "First name is required";
        assert lastName != null : "Last name is required";
        assert hashedPassword != null : "Hashed password is required";
        assert roles != null && !roles.isEmpty() : "Roles set is required";
        this.id = null;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hashedPassword = hashedPassword;
        this.roles = roles;
    }

    public boolean passwordMatch(String hashedPassword) {
        return this.hashedPassword.equals(hashedPassword);
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }
}
