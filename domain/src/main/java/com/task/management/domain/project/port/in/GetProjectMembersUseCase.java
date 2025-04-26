package com.task.management.domain.project.port.in;

import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.projection.MemberView;

import java.util.List;

public interface GetProjectMembersUseCase {
    List<MemberView> getMembers(UserId actorId, ProjectId projectId) throws UseCaseException;
}
