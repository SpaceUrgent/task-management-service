package com.task.management.persistence.jpa.project;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.annotation.AppComponent;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.project.mapper.ProjectUserMapper;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static java.util.Objects.requireNonNull;

@AppComponent
@RequiredArgsConstructor
public class JpaProjectUserRepositoryAdapter implements UserRepositoryPort {
    private final UserEntityDao userEntityDao;
    private final ProjectUserMapper projectUserMapper = ProjectUserMapper.INSTANCE;

    @Override
    public Optional<ProjectUser> find(ProjectUserId id) {
        requireNonNull(id, "User id is required");
        return userEntityDao.findById(id.value()).map(projectUserMapper::toModel);
    }

    @Override
    public Optional<ProjectUser> find(Email email) {
        emailRequired(email);
        return userEntityDao.findByEmail(email.value()).map(projectUserMapper::toModel);
    }
}
