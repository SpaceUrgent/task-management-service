package com.task.management.application.port.out;

public interface PasswordEncryptor {
    String encrypt(char[] password);
}
