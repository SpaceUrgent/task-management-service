package com.task.managment.web.security;

import com.task.management.application.model.User;
import com.task.management.application.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        requireNonNull(email, "Email is required");
        final var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email '%s' not found".formatted(email)));
        return null;
    }
}
