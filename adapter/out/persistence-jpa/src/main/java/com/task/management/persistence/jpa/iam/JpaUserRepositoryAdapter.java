package com.task.management.persistence.jpa.iam;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.iam.model.User;
import com.task.management.domain.iam.model.UserCredentials;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.iam.port.out.UserCredentialsPort;
import com.task.management.domain.iam.port.out.UserRepositoryPort;
import com.task.management.persistence.jpa.common.mapper.UserInfoMapper;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.iam.mapper.UserCredentialsMapper;
import com.task.management.persistence.jpa.iam.mapper.UserMapper;

import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
public class JpaUserRepositoryAdapter implements UserRepositoryPort,
                                                 UserCredentialsPort {
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserInfoMapper userInfoMapper = UserInfoMapper.INSTANCE;
    private final UserCredentialsMapper userCredentialsMapper = UserCredentialsMapper.INSTANCE;
    private final UserEntityDao userEntityDao;

    public JpaUserRepositoryAdapter(UserEntityDao userEntityDao) {
        this.userEntityDao = userEntityDao;
    }

    @Override
    public User save(final User user) {
        parameterRequired(user, "User");
        var entity = userMapper.toEntity(user);
        userEntityDao.save(entity);
        return userMapper.toModel(entity);
    }

    @Override
    public Optional<UserInfo> find(UserId id) {
        parameterRequired(id, "User id");
        return userEntityDao.findById(id.value())
                .map(userInfoMapper::toModel);
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
