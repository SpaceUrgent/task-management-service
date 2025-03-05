package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.ProjectPreview;
import com.task.management.domain.project.model.ProjectUserId;

import java.util.List;

public interface FindProjectsByMemberPort {
    List<ProjectPreview> findProjectsByMember(ProjectUserId memberId);
}
