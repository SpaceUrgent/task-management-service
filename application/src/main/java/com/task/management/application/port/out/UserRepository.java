package com.task.management.application.port.out;

import com.task.management.application.model.User;

public interface UserRepository {
    User add(User user);

    boolean emailExists(String email);
}
