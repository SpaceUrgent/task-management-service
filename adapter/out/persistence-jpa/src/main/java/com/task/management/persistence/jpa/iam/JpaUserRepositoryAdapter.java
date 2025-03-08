package com.task.management.persistence.jpa.iam;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.iam.model.User;
import com.task.management.domain.common.UserCredentials;
import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;
import com.task.management.domain.common.interfaces.UserCredentialsPort;
import com.task.management.domain.iam.port.out.UserRepositoryPort;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.iam.mapper.UserCredentialsMapper;
import com.task.management.persistence.jpa.iam.mapper.UserMapper;
import com.task.management.persistence.jpa.iam.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepositoryPort,
                                                 UserCredentialsPort {
    private final UserEntityDao userEntityDao;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserProfileMapper userProfileMapper = UserProfileMapper.INSTANCE;
    private final UserCredentialsMapper userCredentialsMapper = UserCredentialsMapper.INSTANCE;

    @Override
    public User save(final User user) {
        parameterRequired(user, "User");
        var entity = userMapper.toEntity(user);
        userEntityDao.save(entity);
        return userMapper.toModel(entity);
    }

    @Override
    public Optional<UserProfile> findUserProfile(UserId id) {
        parameterRequired(id, "User id");
        return userEntityDao.findById(id.value()).map(userProfileMapper::toModel);
    }

    @Override
    public boolean emailExists(Email email) {
        emailRequired(email);
        return userEntityDao.existsByEmail(email.value());
    }

    @Override
    public Optional<UserCredentials> findByEmail(Email email) {
        emailRequired(email);
        return userEntityDao.findByEmail(email.value()).map(userCredentialsMapper::toModel);
    }
}
