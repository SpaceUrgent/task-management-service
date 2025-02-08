package com.task.management.application.project.port.out;

import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUser;

import java.util.List;

public interface FindProjectMembersPort {
    List<ProjectUser> findMembers(ProjectId id);
}
