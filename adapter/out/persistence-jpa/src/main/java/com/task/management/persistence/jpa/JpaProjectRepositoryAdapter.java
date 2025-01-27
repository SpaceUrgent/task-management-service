package com.task.management.persistence.jpa;

import com.task.management.application.common.PageQuery;
import com.task.management.application.model.Project;
import com.task.management.application.model.UserId;
import com.task.management.application.port.out.AddProjectPort;
import com.task.management.application.port.out.FindProjectsByMemberPort;
import com.task.management.persistence.jpa.mapper.ProjectMapper;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class JpaProjectRepositoryAdapter implements AddProjectPort,
                                                    FindProjectsByMemberPort {
    private final JpaProjectRepository jpaProjectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public Project add(final Project project) {
        requireNonNull(project, "Project is required");
        var projectEntity = projectMapper.toEntity(project);
        projectEntity = jpaProjectRepository.save(projectEntity);
        return projectMapper.toModel(projectEntity);
    }

    @Override
    public List<Project> findProjectsByMember(UserId member, PageQuery page) {
        requireNonNull(member, "Member id is required");
        requireNonNull(page, "Page is required");
        return jpaProjectRepository.findByMember(member.value(), JpaPage.of(page)).get()
                .map(projectMapper::toModel)
                .toList();
    }
}
