package com.task.managment.web.security;

import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.iam.model.objectvalue.UserCredentials;
import com.task.management.domain.common.model.objectvalue.UserId;
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
        final var mockUserCredentials = new UserCredentials(new UserId(annotation.id()), new Email(annotation.email()), annotation.password());
//                .id(new UserId(annotation.id()))
//                .email(annotation.email())
//                .firstName(annotation.firstName())
//                .lastName(annotation.lastName())
//                .encryptedPassword(annotation.password())
//                .build();
        final var mockSecuredUser = new SecuredUser(mockUserCredentials);
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
