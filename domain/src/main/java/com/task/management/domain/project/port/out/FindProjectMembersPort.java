package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUser;

import java.util.List;

public interface FindProjectMembersPort {
    List<ProjectUser> findMembers(ProjectId id);
}
