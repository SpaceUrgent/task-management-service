package com.task.management.spring.security;

import com.task.managment.web.security.SecuredUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class UserAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final var username = authentication.getName();
        final var password = authentication.getCredentials().toString();
        final var securedUser = (SecuredUser) userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, securedUser.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(securedUser.getUsername(), securedUser.getPassword(), securedUser.getAuthorities());
        token.setDetails(securedUser);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
