package com.task.management.spring.security;

import com.task.management.application.shared.annotation.UseCase;
import com.task.management.application.iam.port.out.UserCredentialsPort;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.managment.web.security.SecuredUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserCredentialsPort findUserCredentialsPort;

    @UseCase
    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        requireNonNull(email, "Email is required");
        final var userCredentials = findUserCredentialsPort.findByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("User with email '%s' not found".formatted(email)));
        return new SecuredUser(userCredentials);
    }
}
