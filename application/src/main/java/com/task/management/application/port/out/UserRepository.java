package com.task.management.application.port.out;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);

    Optional<User> findByEmail(String email);

    User add(User user);

    boolean emailExists(String email);
}
