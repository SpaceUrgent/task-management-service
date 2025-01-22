package com.task.managment.web.security;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class SecuredUser implements UserDetails {
    private final User user;

    public SecuredUser(User user) {
        this.user = requireNonNull(user);
    }

    public UserId getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return user.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }
}
