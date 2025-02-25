package com.task.management.persistence.jpa;

import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.port.out.FindProjectMemberPort;
import com.task.management.application.project.port.out.FindProjectMembersPort;
import com.task.management.application.project.port.out.FindProjectUserByEmailPort;
import com.task.management.application.project.port.out.FindProjectUserByIdPort;
import com.task.management.persistence.jpa.mapper.ProjectUserMapper;
import com.task.management.persistence.jpa.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaProjectUserRepositoryAdapter implements FindProjectUserByIdPort,
                                                        FindProjectUserByEmailPort,
                                                        FindProjectMemberPort,
                                                        FindProjectMembersPort {
    private final JpaUserRepository jpaUserRepository;
    private final ProjectUserMapper projectUserMapper;

    @Override
    public Optional<ProjectUser> findMember(ProjectUserId memberId, ProjectId projectId) {
        requireNonNull(memberId, "Member id is required");
        requireNonNull(projectId, "Project id is required");
        return jpaUserRepository.findMember(memberId.value(), projectId.value()).map(projectUserMapper::toModel);
    }

    @Override
    public List<ProjectUser> findMembers(ProjectId id) {
        requireNonNull(id, "Project id is required");
        return jpaUserRepository.findByProject(id.value())
                .stream()
                .map(projectUserMapper::toModel)
                .toList();
    }

    @Override
    public Optional<ProjectUser> find(ProjectUserId id) {
        requireNonNull(id, "User id is required");
        return jpaUserRepository.findById(id.value()).map(projectUserMapper::toModel);
    }

    @Override
    public Optional<ProjectUser> find(String email) {
        requireNonNull(email, "Email is required");
        return jpaUserRepository.findByEmail(email).map(projectUserMapper::toModel);
    }
}
