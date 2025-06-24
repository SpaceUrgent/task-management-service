package com.task.management.application.project.port.in;

import com.task.management.application.project.command.CreateProjectCommand;
import com.task.management.application.project.projection.ProjectDetails;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.application.shared.UseCaseException;
import com.task.management.application.project.command.AddTaskStatusCommand;
import com.task.management.application.project.command.UpdateProjectCommand;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.objectvalue.ProjectId;

import java.util.List;

public interface ProjectUseCase {
    List<ProjectPreview> getAvailableProjects(UserId actorId);

    ProjectDetails getProjectDetails(UserId actorId, ProjectId projectId) throws UseCaseException;

    void createProject(UserId actorId, CreateProjectCommand command) throws UseCaseException;

    void updateProject(UserId actorId, ProjectId projectId, UpdateProjectCommand command) throws UseCaseException;

    void addTaskStatus(UserId actorId, ProjectId projectId, AddTaskStatusCommand command) throws UseCaseException;

    void removeTaskStatus(UserId actorId, ProjectId projectId, String statusName) throws UseCaseException;

    void addMember(UserId actorId, ProjectId projectId, Email email) throws UseCaseException;

    void leaveProject(UserId actorId, ProjectId projectId) throws UseCaseException;

    void excludeMember(UserId actorId, ProjectId projectId, UserId memberId) throws UseCaseException;
}
