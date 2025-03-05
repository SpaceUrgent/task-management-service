package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;

import java.util.List;

public interface GetProjectMembersUseCase {
    List<ProjectUser> getMembers(ProjectUserId actorId, ProjectId projectId) throws UseCaseException;
}
