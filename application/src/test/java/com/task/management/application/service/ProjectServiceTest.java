package com.task.management.application.service;

import com.task.management.application.model.UserId;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.out.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    private final static String PROJECT_TITLE = "New Project";
    private final static String PROJECT_DESCRIPTION = "Project description";

    @Mock
    private ValidationService validationService;
    @Mock
    private ProjectRepository projectRepository;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() {
        final var givenUserId = new UserId(10L);
        final var givenCreateProjectDto = getCreateProjectDto();
        doAnswer(invocation -> invocation.getArgument(0)).when(projectRepository).add(any());
        final var created = projectService.createProject(givenUserId, givenCreateProjectDto);
        assertEquals(givenCreateProjectDto.getTitle(), created.getTitle());
        assertEquals(givenCreateProjectDto.getDescription(), created.getDescription());
        assertEquals(givenUserId, created.getOwner());
    }

    private static CreateProjectDto getCreateProjectDto() {
        var createProjectDto = new CreateProjectDto();
        createProjectDto.setTitle(PROJECT_TITLE);
        createProjectDto.setDescription(PROJECT_DESCRIPTION);
        return createProjectDto;
    }
}