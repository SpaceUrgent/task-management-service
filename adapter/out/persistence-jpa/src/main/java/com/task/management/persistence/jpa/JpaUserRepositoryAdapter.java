package com.task.management.persistence.jpa;

import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.out.AddUserPort;
import com.task.management.application.port.out.EmailExistsPort;
import com.task.management.application.port.out.FindUserPort;
import com.task.management.persistence.jpa.mapper.UserMapper;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements AddUserPort,
                                                 EmailExistsPort,
                                                 FindUserPort {
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(UserId id) {
        requireNonNull(id, "User id is required");
        return jpaUserRepository.findById(id.value()).map(userMapper::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        requireNonNull(email, "Email is required");
        return jpaUserRepository.findByEmail(email).map(userMapper::toModel);
    }

    @Override
    public User add(final User user) {
        requireNonNull(user, "User is required");
        assert user.getId() == null : "New user is required";
        var userEntity = userMapper.toEntity(user);
        userEntity = jpaUserRepository.save(userEntity);
        return userMapper.toModel(userEntity);
    }

    @Override
    public boolean emailExists(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
}
