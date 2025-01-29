package com.task.managment.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.common.PageQuery;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectDetails;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.AddProjectMemberByEmailUseCase;
import com.task.management.application.port.in.CreateProjectUseCase;
import com.task.management.application.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.port.in.GetProjectDetailsUseCase;
import com.task.management.application.port.in.UpdateProjectUseCase;
import com.task.management.application.port.in.dto.CreateProjectDto;
import com.task.management.application.port.in.dto.UpdateProjectDto;
import com.task.managment.web.WebTest;
import com.task.managment.web.dto.EmailDto;
import com.task.managment.web.dto.PageDto;
import com.task.managment.web.dto.ProjectDetailsDto;
import com.task.managment.web.dto.ProjectDto;
import com.task.managment.web.dto.UserDto;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static com.task.managment.web.security.MockUser.DEFAULT_USER_ID_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    @MockBean
    private GetAvailableProjectsUseCase getAvailableProjectsUseCase;
    @MockBean
    private GetProjectDetailsUseCase getProjectDetailsUseCase;
    @MockBean
    private UpdateProjectUseCase updateProjectUseCase;
    @MockBean
    private AddProjectMemberByEmailUseCase addProjectMemberByEmailUseCase;

    @MockUser
    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() throws Exception {
        final var createProjectDto = getCreateProjectDto();
        final var expectedOwnerId = new UserId(DEFAULT_USER_ID_VALUE);
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

    @MockUser
    @Test
    void getAvailableProjects_shouldReturnDefaultProjectPage_whenNoRequestParamsPresent() throws Exception {
        final var expectedPageNumber = 1;
        final var expectedPageSize = 20;
        final var expectedProjects = getTestProjects(expectedPageSize);
        doReturn(expectedProjects).when(getAvailableProjectsUseCase)
                .getAvailableProjects(
                        eq(new UserId(DEFAULT_USER_ID_VALUE)),
                        eq(new PageQuery(expectedPageNumber, expectedPageSize))
                );
        final var responseBody = mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PageDto<ProjectDto>>() {});
        assertEquals(expectedPageNumber, result.getCurrentPage());
        assertEquals(expectedPageSize, result.getPageSize());
        final var resultData = result.getData();
        for (int i = 0; i < expectedProjects.size(); i++) {
            assertMatches(expectedProjects.get(i), resultData.get(i));
        }
    }

    @MockUser
    @Test
    void getAvailableProjects_shouldReturnAppropriateProjectPage_whenPageParamsPresent() throws Exception {
        final var expectedPageNumber = 3;
        final var expectedPageSize = 5;
        final var expectedProjects = getTestProjects(expectedPageSize);
        doReturn(expectedProjects).when(getAvailableProjectsUseCase)
                .getAvailableProjects(
                        eq(new UserId(DEFAULT_USER_ID_VALUE)),
                        eq(new PageQuery(expectedPageNumber, expectedPageSize))
                );
        final var responseBody = mockMvc.perform(get("/api/projects")
                        .param("pageNumber", String.valueOf(expectedPageNumber))
                        .param("pageSize", String.valueOf(expectedPageSize)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PageDto<ProjectDto>>() {});
        assertEquals(expectedPageNumber, result.getCurrentPage());
        assertEquals(expectedPageSize, result.getPageSize());
        final var resultData = result.getData();
        for (int i = 0; i < expectedProjects.size(); i++) {
            assertMatches(expectedProjects.get(i), resultData.get(i));
        }
    }

    @MockUser
    @Test
    void getProjectDetails_shouldReturnProjectDetails_whenAllConditionsMet() throws Exception {
        final var projectDetails = getTestProjectDetails();
        final var givenProjectId = projectDetails.project().getId();
        doReturn(projectDetails).when(getProjectDetailsUseCase)
                .getProjectDetails(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId));
        final var responseBody = mockMvc.perform(get("/api/projects/{givenProjectId}", givenProjectId.value()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var projectDetailsReceived = objectMapper.readValue(responseBody, ProjectDetailsDto.class);
        assertMatches(projectDetails, projectDetailsReceived);
    }

    @MockUser
    @Test
    void getProjectDetails_shouldReturnNotFound_whenEntityNotFound() throws Exception {
        final var givenProjectId = randomProjectId();
        final var errorMessage = "Project not found";
        doThrow(new EntityNotFoundException(errorMessage)).when(getProjectDetailsUseCase)
                .getProjectDetails(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId));
        mockMvc.perform(get("/api/projects/{givenProjectId}", givenProjectId.value()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Entity not found"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/projects/%d".formatted(givenProjectId.value())));
    }

    @MockUser
    @Test
    void getProjectDetails_shouldReturnForbidden_whenNotEnoughPrivileges() throws Exception {
        final var givenProjectId = randomProjectId();
        final var errorMessage = "Not enough privileges";
        doThrow(new InsufficientPrivilegesException(errorMessage)).when(getProjectDetailsUseCase)
                .getProjectDetails(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId));
        mockMvc.perform(get("/api/projects/{givenProjectId}", givenProjectId.value()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Action not allowed"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/projects/%d".formatted(givenProjectId.value())));
    }

    @MockUser
    @Test
    void updateProjectInfo_shouldReturnUpdatedProject_whenAllConditionsMet() throws Exception {
        final var givenUpdateDto = getUpdateProjectDto();
        final var expectedProject = getTestProject();
        expectedProject.setTitle(givenUpdateDto.getTitle());
        expectedProject.setDescription(givenUpdateDto.getDescription());
        final var givenProjectId = expectedProject.getId();
        doReturn(expectedProject).when(updateProjectUseCase)
                        .updateProject(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId), eq(givenUpdateDto));
        mockMvc.perform(patch("/api/projects/{projectId}", givenProjectId.value())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedProject.getId().value()))
                .andExpect(jsonPath("$.title").value(expectedProject.getTitle()))
                .andExpect(jsonPath("$.description").value(expectedProject.getDescription()));
    }

    @MockUser
    @Test
    void updateProjectInfo_shouldReturnBadRequest_whenRequestBodyIsMissing() throws Exception {
        final var givenProjectId = randomProjectId();
        mockMvc.perform(patch("/api/projects/{projectId}", givenProjectId.value()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Required request body is missing"))
                .andExpect(jsonPath("$.path").value("/api/projects/%d".formatted(givenProjectId.value())));
    }

    @MockUser
    @Test
    void updateProjectInfo_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
        final var updateProjectDto = new UpdateProjectDto();
        updateProjectDto.setTitle(" ");
        updateProjectDto.setDescription(" ");
        final var givenProjectId = randomProjectId();
        mockMvc.perform(patch("/api/projects/{projectId}", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProjectDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Request validation error"))
                .andExpect(jsonPath("$.errors.title").value("Title is required"))
                .andExpect(jsonPath("$.errors.description").value("Description is required"))
                .andExpect(jsonPath("$.path").value("/api/projects/%d".formatted(givenProjectId.value())));
    }

    @MockUser
    @Test
    void updateProjectInfo_shouldReturnNotFound_whenEntityNotFound() throws Exception {
        final var errorMessage = "Project not found";
        final var givenProjectId = randomProjectId();
        final var updateDto = getUpdateProjectDto();
        doThrow(new EntityNotFoundException(errorMessage)).when(updateProjectUseCase)
                .updateProject(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId), eq(updateDto));
        mockMvc.perform(patch("/api/projects/{givenProjectId}", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Entity not found"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/projects/%d".formatted(givenProjectId.value())));
    }

    @MockUser
    @Test
    void updateProjectInfo_shouldReturnForbidden_whenNotEnoughPrivileges() throws Exception {
        final var errorMessage = "Project not found";
        final var givenProjectId = randomProjectId();
        final var updateDto = getUpdateProjectDto();
        doThrow(new InsufficientPrivilegesException(errorMessage)).when(updateProjectUseCase)
                .updateProject(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId), eq(updateDto));
        mockMvc.perform(patch("/api/projects/{givenProjectId}", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Action not allowed"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/projects/%d".formatted(givenProjectId.value())));
    }

    @MockUser
    @Test
    void addProjectMember_shouldAddMember_whenAllConditionsMet() throws Exception {
        final var givenRequestBody = new EmailDto();
        givenRequestBody.setEmail("member@mail.com");
        final var givenProjectId = randomProjectId();

        mockMvc.perform(put("/api/projects/{projectId}/members", givenProjectId.value())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(givenRequestBody)))
                .andExpect(status().isOk());

        verify(addProjectMemberByEmailUseCase)
                .addMember(eq(new UserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId), eq(givenRequestBody.getEmail()));
    }

    @MockUser
    @Test
    void addProjectMember_shouldReturnBadRequest_whenRequestBodyIsMissing() throws Exception {
        final var givenProjectId = randomProjectId();
        mockMvc.perform(put("/api/projects/{projectId}/members", givenProjectId.value()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Required request body is missing"))
                .andExpect(jsonPath("$.path").value("/api/projects/%d/members".formatted(givenProjectId.value())));

        verifyNoMoreInteractions(addProjectMemberByEmailUseCase);
    }

    @MockUser
    @Test
    void addProjectMember_shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
        final var givenRequestBody = new EmailDto();
        final var givenProjectId = randomProjectId();

        mockMvc.perform(put("/api/projects/{projectId}/members", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Request validation error"))
                .andExpect(jsonPath("$.errors.email").value("Email is required"))
                .andExpect(jsonPath("$.path").value("/api/projects/%d/members".formatted(givenProjectId.value())));

        verifyNoInteractions(addProjectMemberByEmailUseCase);
    }

    @MockUser
    @Test
    void addProjectMember_shouldReturnNotFound_whenEntityNotFound() throws Exception {
        final var errorMessage = "Project not found";
        final var givenRequestBody = new EmailDto();
        givenRequestBody.setEmail("member@mail.com");
        final var givenProjectId = randomProjectId();
        doThrow(new EntityNotFoundException(errorMessage)).when(addProjectMemberByEmailUseCase)
                .addMember(any(), any(), any());
        mockMvc.perform(put("/api/projects/{projectId}/members", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequestBody)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Entity not found"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/projects/%d/members".formatted(givenProjectId.value())));
    }

    @MockUser
    @Test
    void addProjectMember_shouldReturnForbidden_whenNotEnoughPrivileges() throws Exception {
        final var errorMessage = "Not enough privileges";
        final var givenRequestBody = new EmailDto();
        givenRequestBody.setEmail("member@mail.com");
        final var givenProjectId = randomProjectId();
        doThrow(new InsufficientPrivilegesException(errorMessage)).when(addProjectMemberByEmailUseCase)
                .addMember(any(), any(), any());
        mockMvc.perform(put("/api/projects/{projectId}/members", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequestBody)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Action not allowed"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/projects/%d/members".formatted(givenProjectId.value())));
    }

    private void assertMatches(ProjectDetails expected, ProjectDetailsDto actual) {
        assertMatches(expected.project(), actual.getProject());
        assertMatches(expected.owner(), actual.getOwner());
        assertMatches(expected.members(), actual.getMembers());
    }

    private void assertMatches(List<User> expected, List<UserDto> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private void assertMatches(User expected, UserDto actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
    }

    private void assertMatches(Project expected, ProjectDto actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
    }

    private static UpdateProjectDto getUpdateProjectDto() {
        final var updateDto = new UpdateProjectDto();
        updateDto.setTitle("New title");
        updateDto.setDescription("New Description");
        return updateDto;
    }

    private static CreateProjectDto getCreateProjectDto() {
        final var createProjectDto = new CreateProjectDto();
        createProjectDto.setTitle(PROJECT_TITLE);
        createProjectDto.setDescription(PROJECT_DESCRIPTION);
        return createProjectDto;
    }

    private static ProjectDetails getTestProjectDetails() {
        final var owner = getTestUser();
        return ProjectDetails.builder()
                .project(getTestProject(owner.getId()))
                .owner(owner)
                .members(List.of(owner))
                .build();
    }

    private static User getTestUser() {
        final var userIdValue = randomLong();
        return User.builder()
                .id(new UserId(userIdValue))
                .firstName("Name-%d".formatted(userIdValue))
                .lastName("Last-Name-%d".formatted(userIdValue))
                .email("user-%d@mail.com")
                .encryptedPassword("encryptedPassword")
                .build();
    }

    private static List<Project> getTestProjects(int size) {
        return IntStream.range(0, size).mapToObj(value -> getTestProject()).toList();
    }

    private static Project getTestProject() {
        return getTestProject(new UserId(randomLong()));
    }

    private static Project getTestProject(final UserId ownerId) {
        final var projectId = randomProjectId();
        return Project.builder()
                .id(projectId)
                .title("Project %d".formatted(projectId.value()))
                .description("Project %d description".formatted(projectId.value()))
                .owner(ownerId)
                .build();
    }

    private static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    private static Long randomLong() {
        return new Random().nextLong();
    }
}