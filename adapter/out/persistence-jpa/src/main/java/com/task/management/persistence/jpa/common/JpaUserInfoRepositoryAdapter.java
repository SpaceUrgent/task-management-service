package com.task.management.persistence.jpa.common;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.common.port.out.UserInfoRepositoryPort;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.management.persistence.jpa.common.mapper.UserInfoMapper;
import com.task.management.persistence.jpa.dao.UserEntityDao;

import java.util.Optional;

import static com.task.management.domain.shared.validation.Validation.emailRequired;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@AppComponent
public class JpaUserInfoRepositoryAdapter implements UserInfoRepositoryPort {
    protected final UserInfoMapper userInfoMapper = UserInfoMapper.INSTANCE;
    protected final UserEntityDao userEntityDao;

    public JpaUserInfoRepositoryAdapter(UserEntityDao userEntityDao) {
        this.userEntityDao = userEntityDao;
    }

    @Override
    public Optional<UserInfo> find(UserId id) {
        parameterRequired(id, "User id");
        return userEntityDao.findById(id.value())
                .map(userInfoMapper::toModel);
    }

    @Override
    public Optional<UserInfo> find(Email email) {
        emailRequired(email);
        return userEntityDao.findByEmail(email.value())
                .map(userInfoMapper::toModel);
    }
}
