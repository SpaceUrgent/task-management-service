package com.task.management.password.encryption;

import com.task.management.application.port.out.PasswordEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@RequiredArgsConstructor
public class BCryptPasswordEncryptorAdapter implements PasswordEncryptor {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encrypt(char[] password) {
        Objects.requireNonNull(password, "Password is required");
        return passwordEncoder.encode(new StringBuffer().append(password));
    }
}
