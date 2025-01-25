package com.task.management.application.port.out;

import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.ProjectId;

import java.util.Optional;

public interface FindProjectDetailsPort {
    Optional<ProjectDetails> findProjectDetails(ProjectId projectId);
}
