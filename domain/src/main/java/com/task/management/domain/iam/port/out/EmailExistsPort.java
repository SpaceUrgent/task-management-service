package com.task.management.domain.iam.port.out;

public interface EmailExistsPort {
    boolean emailExists(String email);
}
