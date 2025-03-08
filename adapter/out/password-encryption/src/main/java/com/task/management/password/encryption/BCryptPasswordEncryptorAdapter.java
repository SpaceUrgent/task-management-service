package com.task.management.password.encryption;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.validation.Validation;
import com.task.management.domain.iam.port.out.EncryptPasswordPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AppComponent
@RequiredArgsConstructor
public class BCryptPasswordEncryptorAdapter implements EncryptPasswordPort {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encrypt(char[] password) {
        Validation.parameterRequired(password, "Password");
        return passwordEncoder.encode(new StringBuffer().append(password));
    }
}
