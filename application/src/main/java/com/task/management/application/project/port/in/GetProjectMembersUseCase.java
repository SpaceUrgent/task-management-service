package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;

import java.util.List;

public interface GetProjectMembersUseCase {
    List<ProjectUser> getMembers(ProjectUserId actorId, ProjectId projectId) throws UseCaseException;
}
