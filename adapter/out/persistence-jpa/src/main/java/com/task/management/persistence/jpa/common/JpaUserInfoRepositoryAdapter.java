package com.task.management.persistence.jpa.common;

import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.model.UserInfo;
import com.task.management.domain.common.port.out.UserInfoRepositoryPort;
import com.task.management.persistence.jpa.common.mapper.UserInfoMapper;
import com.task.management.persistence.jpa.dao.UserEntityDao;

import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.emailRequired;

@AppComponent
public class JpaUserInfoRepositoryAdapter implements UserInfoRepositoryPort {
    protected final UserInfoMapper userInfoMapper = UserInfoMapper.INSTANCE;
    protected final UserEntityDao userEntityDao;

    public JpaUserInfoRepositoryAdapter(UserEntityDao userEntityDao) {
        this.userEntityDao = userEntityDao;
    }

    @Override
    public Optional<UserInfo> find(Email email) {
        emailRequired(email);
        return userEntityDao.findByEmail(email.value())
                .map(userInfoMapper::toModel);
    }
}
