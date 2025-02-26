package com.task.management.persistence.jpa;

import com.task.management.application.iam.model.User;
import com.task.management.application.iam.model.UserId;
import com.task.management.application.iam.model.UserProfile;
import com.task.management.application.iam.port.out.EmailExistsPort;
import com.task.management.application.iam.port.out.FindUserProfileByIdPort;
import com.task.management.application.iam.port.out.AddUserPort;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.mapper.Mappers;
import com.task.management.persistence.jpa.mapper.UserMapper;
import com.task.management.persistence.jpa.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static java.util.Objects.requireNonNull;


@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements AddUserPort,
                                                 FindUserProfileByIdPort,
                                                 EmailExistsPort {
    private final UserEntityDao jpaUserRepository;
    private final UserMapper userMapper = Mappers.userMapper;
    private final UserProfileMapper userProfileMapper = Mappers.userProfileMapper;

    @Override
    public User add(final User user) {
        requireNonNull(user, "User is required");
        var entity = userMapper.toEntity(user);
        jpaUserRepository.save(entity);
        return userMapper.toModel(entity);
    }

    @Override
    public Optional<UserProfile> find(UserId id) {
        requireNonNull(id, "User id is required");
        return jpaUserRepository.findById(id.value()).map(userProfileMapper::toModel);
    }

    @Override
    public boolean emailExists(String email) {
        requireNonNull(email, "Email is required");
        return jpaUserRepository.existsByEmail(email);
    }
}
