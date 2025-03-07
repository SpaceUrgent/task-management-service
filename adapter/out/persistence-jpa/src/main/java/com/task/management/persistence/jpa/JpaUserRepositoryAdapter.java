package com.task.management.persistence.jpa;

import com.task.management.domain.iam.model.User;
import com.task.management.domain.iam.model.UserCredentials;
import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;
import com.task.management.domain.iam.port.out.EmailExistsPort;
import com.task.management.domain.iam.port.out.FindUserCredentialsPort;
import com.task.management.domain.iam.port.out.FindUserProfileByIdPort;
import com.task.management.domain.iam.port.out.AddUserPort;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.mapper.Mappers;
import com.task.management.persistence.jpa.mapper.UserCredentialsMapper;
import com.task.management.persistence.jpa.mapper.UserMapper;
import com.task.management.persistence.jpa.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static java.util.Objects.requireNonNull;


@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements AddUserPort,
                                                 FindUserProfileByIdPort,
                                                 EmailExistsPort,
                                                 FindUserCredentialsPort {
    private final UserEntityDao userEntityDao;
    private final UserMapper userMapper = Mappers.userMapper;
    private final UserProfileMapper userProfileMapper = Mappers.userProfileMapper;
    private final UserCredentialsMapper userCredentialsMapper = Mappers.userCredentialsMapper;

    @Override
    public User add(final User user) {
        requireNonNull(user, "User is required");
        var entity = userMapper.toEntity(user);
        userEntityDao.save(entity);
        return userMapper.toModel(entity);
    }

    @Override
    public Optional<UserProfile> find(UserId id) {
        requireNonNull(id, "User id is required");
        return userEntityDao.findById(id.value()).map(userProfileMapper::toModel);
    }

    @Override
    public boolean emailExists(String email) {
        requireNonNull(email, "Email is required");
        return userEntityDao.existsByEmail(email);
    }

    @Override
    public Optional<UserCredentials> findByEmail(String email) {
        requireNonNull(email, "Email is required");
        return userEntityDao.findByEmail(email).map(userCredentialsMapper::toModel);
    }
}
