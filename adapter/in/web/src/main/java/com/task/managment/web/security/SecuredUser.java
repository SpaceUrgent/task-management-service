package com.task.managment.web.security;

import com.task.management.domain.iam.model.UserCredentials;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class SecuredUser implements UserDetails {
    private final UserCredentials credentials;

    public SecuredUser(UserCredentials credentials) {
        this.credentials = requireNonNull(credentials);
    }

    public Long getId() {
        return credentials.id().value();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return credentials.encryptedPassword();
    }

    @Override
    public String getUsername() {
        return credentials.email().value();
    }
}
