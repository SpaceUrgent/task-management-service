package com.task.management.application.iam.port.out;

public interface EncryptPasswordPort {
    String encrypt(char[] password);

    boolean matches(char[] rawPassword, String encrypted);
}
