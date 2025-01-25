package com.task.managment.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.managment.web.WebTest;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest
class ProjectControllerTest {
    private final static String PROJECT_TITLE = "Project title";
    private final static String PROJECT_DESCRIPTION = "Project description";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private CreateProjectUseCase createProjectUseCase;

    @MockUser
    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() throws Exception {
        final var createProjectDto = getCreateProjectDto();
        final var expectedOwnerId = new UserId(MockUser.DEFAULT_USER_ID_VALUE);
        final var expectedProject = getTestProject(expectedOwnerId);
        doReturn(expectedProject).when(createProjectUseCase).createProject(eq(expectedOwnerId), eq(createProjectDto));
        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createProjectDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedProject.getId().value()))
                .andExpect(jsonPath("$.title").value(expectedProject.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedProject.getDescription()));
    }

    @MockUser
    @Test
    void createProject_shouldReturnBadRequest_whenRequestBodyIsMissing() throws Exception {
        mockMvc.perform(post("/api/projects"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Required request body is missing"))
                .andExpect(jsonPath("$.path").value("/api/projects"));
    }

    @MockUser
    @Test
    void createProject_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .content(objectMapper.writeValueAsBytes(new CreateProjectDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Request validation error"))
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.description").value("Description is required"))
                .andExpect(jsonPath("$.path").value("/api/projects"));
    }

    private static CreateProjectDto getCreateProjectDto() {
        final var createProjectDto = new CreateProjectDto();
        createProjectDto.setTitle(PROJECT_TITLE);
        createProjectDto.setDescription(PROJECT_DESCRIPTION);
        return createProjectDto;
    }

    private static Project getTestProject(UserId expectedOwnerId) {
        return Project.builder()
                .id(new ProjectId(new Random().nextLong()))
                .title(PROJECT_TITLE)
                .description(PROJECT_DESCRIPTION)
                .owner(expectedOwnerId)
                .build();
    }


}