package com.task.management.application.project.port.out;

import com.task.management.application.project.model.ProjectPreview;
import com.task.management.application.project.model.ProjectUserId;

import java.util.List;

public interface FindProjectsByMemberPort {
    List<ProjectPreview> findProjectsByMember(ProjectUserId memberId);
}
