package com.task.management.domain.project.service;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.port.out.FindProjectMemberPort;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.port.out.FindProjectUserByEmailPort;
import com.task.management.domain.project.port.out.FindProjectUserByIdPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.task.management.domain.common.Validation.parameterRequired;

@Slf4j
@RequiredArgsConstructor
public class ProjectUserService  {
    private final FindProjectUserByIdPort findProjectUserByIdPort;
    private final FindProjectUserByEmailPort findProjectUserByEmailPort;
    private final FindProjectMemberPort findProjectMemberPort;

    public ProjectUser getProjectUser(ProjectUserId id) throws UseCaseException.EntityNotFoundException {
        parameterRequired(id, "User id");
        return findProjectUserByIdPort.find(id)
                .orElseThrow(() -> {
                    log.debug("User with id {} not found", id);
                    return new UseCaseException.EntityNotFoundException("User not found");
                });
    }

    public ProjectUser getProjectUser(String email) throws UseCaseException.EntityNotFoundException {
        parameterRequired(email, "Email");
        return findProjectUserByEmailPort.find(email)
                .orElseThrow(() -> {
                    log.debug("User with email {} not found", email);
                    return new UseCaseException.EntityNotFoundException("User with email '%s' not found".formatted(email));
                });
    }

    public boolean isMember(ProjectUserId userId, ProjectId projectId) {
        parameterRequired(userId, "User id");
        parameterRequired(projectId, "Project id");
        return findProjectMemberPort.findMember(userId, projectId).isPresent();
    }

    public Optional<ProjectUser> findProjectMember(ProjectUserId memberId, ProjectId projectId) {
        parameterRequired(memberId, "Member id");
        parameterRequired(projectId, "Project id");
        return findProjectMemberPort.findMember(memberId, projectId);
    }
}
