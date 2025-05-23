package com.task.managment.web.security;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.annotation.UseCase;
import com.task.management.domain.common.interfaces.UserCredentialsPort;
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
