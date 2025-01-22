package com.task.managment.web.security;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class MockUserSecurityContextFactory implements WithSecurityContextFactory<MockUser> {
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SecurityContext createSecurityContext(MockUser annotation) {
        final var context = SecurityContextHolder.createEmptyContext();
        final var mockUser = User.builder()
                .id(new UserId(annotation.id()))
                .email(annotation.email())
                .firstName(annotation.firstName())
                .lastName(annotation.lastName())
                .encryptedPassword(annotation.password())
                .build();
        final var mockSecuredUser = new SecuredUser(mockUser);
        final var authentication = new UsernamePasswordAuthenticationToken(
                mockSecuredUser,
                annotation.password(),
                mockSecuredUser.getAuthorities()
        );
        authentication.setDetails(mockSecuredUser);
        context.setAuthentication(authentication);
        return context;
    }
}
