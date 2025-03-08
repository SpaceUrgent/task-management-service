package com.task.management.persistence.jpa;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.port.out.FindProjectMemberPort;
import com.task.management.domain.project.port.out.FindProjectMembersPort;
import com.task.management.domain.project.port.out.FindProjectUserByEmailPort;
import com.task.management.domain.project.port.out.FindProjectUserByIdPort;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.mapper.Mappers;
import com.task.management.persistence.jpa.mapper.ProjectUserMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaProjectUserRepositoryAdapter implements FindProjectUserByIdPort,
                                                        FindProjectUserByEmailPort,
                                                        FindProjectMemberPort,
                                                        FindProjectMembersPort {
    private final UserEntityDao userEntityDao;
    private final ProjectUserMapper projectUserMapper = Mappers.projectUserMapper;

    @Override
    public Optional<ProjectUser> findMember(ProjectUserId memberId, ProjectId projectId) {
        requireNonNull(memberId, "Member id is required");
        requireNonNull(projectId, "Project id is required");
        return userEntityDao.findMember(memberId.value(), projectId.value()).map(projectUserMapper::toModel);
    }

    @Override
    public List<ProjectUser> findMembers(ProjectId id) {
        requireNonNull(id, "Project id is required");
        return userEntityDao.findByProject(id.value())
                .stream()
                .map(projectUserMapper::toModel)
                .toList();
    }

    @Override
    public Optional<ProjectUser> find(ProjectUserId id) {
        requireNonNull(id, "User id is required");
        return userEntityDao.findById(id.value()).map(projectUserMapper::toModel);
    }

    @Override
    public Optional<ProjectUser> find(String email) {
        requireNonNull(email, "Email is required");
        return userEntityDao.findByEmail(email).map(projectUserMapper::toModel);
    }
}
