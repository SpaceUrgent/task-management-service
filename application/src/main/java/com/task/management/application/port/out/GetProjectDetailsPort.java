package com.task.management.application.port.out;

import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.model.ProjectId;

public interface GetProjectDetailsPort {
    ProjectDetailsDTO getProjectDetails(ProjectId projectId);
}
