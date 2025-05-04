package com.task.managment.web.security;

import com.task.management.application.common.annotation.UseCase;
import com.task.management.application.iam.port.out.UserCredentialsPort;
import com.task.management.domain.common.model.objectvalue.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
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
