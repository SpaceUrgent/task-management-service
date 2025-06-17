package com.task.management.password.encryption;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.iam.port.out.EncryptPasswordPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@AppComponent
public class BCryptPasswordEncryptorAdapter implements EncryptPasswordPort {
    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordEncryptorAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encrypt(char[] password) {
        parameterRequired(password, "Password");
        return passwordEncoder.encode(new StringBuffer().append(password));
    }

    @Override
    public boolean matches(char[] rawPassword, String encrypted) {
        parameterRequired(rawPassword, "Raw password");
        parameterRequired(encrypted, "Encrypted password");
        return passwordEncoder.matches(new StringBuffer().append(rawPassword), encrypted);
    }
}
