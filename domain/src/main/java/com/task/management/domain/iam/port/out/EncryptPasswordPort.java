package com.task.management.domain.iam.port.out;

public interface EncryptPasswordPort {
    String encrypt(char[] password);
}
