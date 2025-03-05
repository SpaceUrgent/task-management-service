package com.task.managment.web.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.common.Page;
import com.task.management.application.common.Sort;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectPreview;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskPreview;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.AddProjectMemberUseCase;
import com.task.management.application.project.port.in.CreateProjectUseCase;
import com.task.management.application.project.port.in.CreateTaskUseCase;
import com.task.management.application.project.port.in.FindTasksUseCase;
import com.task.management.application.project.port.in.GetAvailableProjectsUseCase;
import com.task.management.application.project.port.in.GetProjectMembersUseCase;
import com.task.management.application.project.port.in.UpdateProjectUseCase;
import com.task.management.application.project.port.in.command.CreateProjectCommand;
import com.task.management.application.project.port.in.command.CreateTaskCommand;
import com.task.management.application.project.port.in.query.FindTasksQuery;
import com.task.managment.web.TestUtils;
import com.task.managment.web.WebTest;
import com.task.managment.web.dto.ProjectPreviewDto;
import com.task.managment.web.dto.ProjectUserDto;
import com.task.managment.web.dto.TaskPreviewDto;
import com.task.managment.web.dto.request.CreateProjectRequest;
import com.task.managment.web.dto.request.CreateTaskRequest;
import com.task.managment.web.dto.request.UpdateProjectRequest;
import com.task.managment.web.dto.response.AvailableProjectsResponse;
import com.task.managment.web.dto.response.PagedResponse;
import com.task.managment.web.dto.response.ProjectMembersResponse;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import static com.task.managment.web.TestUtils.PROJECT_USER_ID;
import static com.task.managment.web.TestUtils.randomLong;
import static com.task.managment.web.TestUtils.randomProjectUser;
import static com.task.managment.web.TestUtils.randomProjectUsers;
import static com.task.managment.web.security.MockUser.DEFAULT_USER_ID_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest(controllerClass = ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private GetAvailableProjectsUseCase getAvailableProjectsUseCase;
    @MockBean
    private GetProjectMembersUseCase getProjectMembersUseCase;
    @MockBean
    private CreateProjectUseCase createProjectUseCase;
    @MockBean
    private UpdateProjectUseCase updateProjectUseCase;
    @MockBean
    private AddProjectMemberUseCase addProjectMemberUseCase;
    @MockBean
    private FindTasksUseCase findTasksUseCase;
    @MockBean
    private CreateTaskUseCase createTaskUseCase;

    @MockUser
    @Test
    void getAvailableProjects() throws Exception {
        final var expectedProjects = randomProjectPreviews();
        doReturn(expectedProjects).when(getAvailableProjectsUseCase)
                .getAvailableProjects(eq(new ProjectUserId(DEFAULT_USER_ID_VALUE)));
        final var responseBody = mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, AvailableProjectsResponse.class);
        assertMatches(expectedProjects, result.getData());
    }

    @MockUser
    @Test
    void getProjectMembers() throws Exception {
        final var givenProjectId = randomProjectId();
        final var expectedMembers = randomProjectUsers();
        doReturn(expectedMembers)
                .when(getProjectMembersUseCase)
                .getMembers(eq(new ProjectUserId(DEFAULT_USER_ID_VALUE)), eq(givenProjectId));
        final var responseBody = mockMvc.perform(get("/api/projects/{projectId}/members", givenProjectId.value()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, ProjectMembersResponse.class);
        for (int i = 0; i < expectedMembers.size(); i++) {
            assertMatches(expectedMembers.get(i), result.getData().get(i));
        }
    }

    @MockUser
    @Test
    void createProject() throws Exception {
        final var givenRequest = getCreateProjectRequest();
        mockMvc.perform(post("/api/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isCreated());
        verify(createProjectUseCase).createProject(eq(new ProjectUserId(DEFAULT_USER_ID_VALUE)), argThat(createProjectCommandMatcher(givenRequest)));
    }

    @MockUser
    @Test
    void updateProject() throws Exception {
        final var givenRequest = getUpdateProjectRequest();
        final var givenProjectId = randomProjectId();
        final var expectedCommand = com.task.management.application.port.in.command.UpdateProjectCommand.builder()
                .projectId(givenProjectId)
                .title(givenRequest.getTitle())
                .description(givenRequest.getDescription())
                .build();
        mockMvc.perform(put("/api/projects/{projectId}", givenProjectId.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(givenRequest)))
                        .andExpect(status().isOk());
        verify(updateProjectUseCase).updateProject(eq(new ProjectUserId(DEFAULT_USER_ID_VALUE)), eq(expectedCommand));
    }

    @MockUser
    @Test
    void getTasks_withDefaultParams() throws Exception {
        final var givenProjectId = randomProjectId();
        final var expectedQuery = FindTasksQuery.builder()
                .pageNumber(1)
                .pageSize(50)
                .projectId(givenProjectId)
                .sortByCreatedAt(Sort.Direction.DESC)
                .statusIn(new HashSet<>())
                .build();
        final var expectedTaskPreviews = Page.<TaskPreview>builder()
                .pageNo(expectedQuery.getPageNumber())
                .pageSize(expectedQuery.getPageSize())
                .total(100)
                .totalPages(2)
                .content(randomTaskPreviews(expectedQuery.getPageSize()))
                .build();
        doReturn(expectedTaskPreviews).when(findTasksUseCase).findTasks(eq(PROJECT_USER_ID), eq(expectedQuery));
        final var responseBody = mockMvc.perform(get("/api/projects/{projectId}/tasks", givenProjectId.value()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PagedResponse<TaskPreviewDto>>() {});
        assertMatches(expectedTaskPreviews, result);
    }

    @MockUser
    @Test
    void getTasks_withCustomParams() throws Exception {
        final var givenProjectId = randomProjectId();
        final var givenAssigneeId = randomProjectUser().id();
        final var givenTaskStatus = Set.of(TaskStatus.DONE, TaskStatus.IN_PROGRESS);
        final var expectedQuery = FindTasksQuery.builder()
                .pageNumber(2)
                .pageSize(10)
                .projectId(givenProjectId)
                .assigneeId(givenAssigneeId)
                .statusIn(givenTaskStatus)
                .sortByCreatedAt(Sort.Direction.ASC)
                .build();
        final var totalTasks = 100;
        final var expectedTaskPreviews = Page.<TaskPreview>builder()
                .pageNo(expectedQuery.getPageNumber())
                .pageSize(expectedQuery.getPageSize())
                .total(totalTasks)
                .totalPages(totalTasks / expectedQuery.getPageSize())
                .content(randomTaskPreviews(expectedQuery.getPageSize()))
                .build();
        doReturn(expectedTaskPreviews).when(findTasksUseCase).findTasks(eq(PROJECT_USER_ID), eq(expectedQuery));
        final var responseBody = mockMvc.perform(get("/api/projects/{projectId}/tasks", givenProjectId.value())
                        .param("page", "2")
                        .param("size", "10")
                        .param("assigneeId", givenAssigneeId.value().toString())
                        .param("status", givenTaskStatus.stream().map(TaskStatus::value).toList().toArray(new String[]{}))
                        .param("sortBy", "createdAt:ASC"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var result = objectMapper.readValue(responseBody, new TypeReference<PagedResponse<TaskPreviewDto>>() {});
        assertMatches(expectedTaskPreviews, result);
    }

    @MockUser
    @Test
    void createTask() throws Exception {
        final var givenRequest = getCreateTaskRequest();
        final var givenProjectId = randomProjectId();
        final var expectedCommand = CreateTaskCommand.builder()
                .projectId(givenProjectId)
                .title(givenRequest.getTitle())
                .description(givenRequest.getDescription())
                .assigneeId(new ProjectUserId(givenRequest.getAssigneeId()))
                .build();

        mockMvc.perform(post("/api/projects/{projectId}/tasks", givenProjectId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isCreated());

        verify(createTaskUseCase).createTask(eq(PROJECT_USER_ID), eq(expectedCommand));
    }

    private void assertMatches(Page<TaskPreview> expected, PagedResponse<TaskPreviewDto> actual) {
        assertEquals(expected.pageNo(), actual.getCurrentPage());
        assertEquals(expected.pageSize(), actual.getPageSize());
        assertEquals(expected.total().longValue(), actual.getTotal());
        assertEquals(expected.totalPages().longValue(), actual.getTotalPages());
        for (int i = 0; i < expected.content().size(); i++) {
            assertMatches(expected.content().get(i), actual.getData().get(i));
        }
    }

    private void assertMatches(TaskPreview expected, TaskPreviewDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.createdAt(), actual.getCreatedAt());
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.status().value(), actual.getStatus());
        assertMatches(expected.assignee(), actual.getAssignee());
    }

    private CreateTaskRequest getCreateTaskRequest() {
        final var request = new CreateTaskRequest();
        request.setTitle("New task");
        request.setDescription("New task description");
        request.setAssigneeId(randomLong());
        return request;
    }

    private void assertMatches(List<ProjectPreview> expected, List<ProjectPreviewDto> actual) {
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private void assertMatches(ProjectPreview expected, ProjectPreviewDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.title(), actual.getTitle());
        assertMatches(expected.owner(), actual.getOwner());
    }

    private void assertMatches(ProjectUser expected, ProjectUserDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.email(), actual.getEmail());
        assertEquals(expected.firstName(), actual.getFirstName());
        assertEquals(expected.lastName(), actual.getLastName());
    }

    private CreateProjectRequest getCreateProjectRequest() {
        final var request = new CreateProjectRequest();
        request.setTitle("New project");
        request.setDescription("New project description");
        return request;
    }

    private static UpdateProjectRequest getUpdateProjectRequest() {
        final var request = new UpdateProjectRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        return request;
    }

    private List<ProjectPreview> randomProjectPreviews() {
        return IntStream.range(0, 20)
                .mapToObj(value -> randomProjectPreview())
                .toList();
    }

    private ProjectPreview randomProjectPreview() {
        final var randomProjectIdValue = randomLong();
        return ProjectPreview.builder()
                .id(new ProjectId(randomProjectIdValue))
                .title("Title %d".formatted(randomProjectIdValue))
                .owner(TestUtils.randomProjectUser())
                .build();
    }

    private List<TaskPreview> randomTaskPreviews(int total) {
        return IntStream.range(0, total)
                .mapToObj(value -> randomTaskPreview())
                .toList();
    }

    private TaskPreview randomTaskPreview() {
        final var idValue = randomLong();
        return TaskPreview.builder()
                .id(new TaskId(idValue))
                .createdAt(Instant.now())
                .title("Task %d".formatted(idValue))
                .status(TaskStatus.IN_PROGRESS)
                .assignee(randomProjectUser())
                .build();
    }

    private ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    private static ArgumentMatcher<CreateProjectCommand> createProjectCommandMatcher(CreateProjectRequest expected) {
        return command -> Objects.equals(expected.getTitle(), command.title())
                && Objects.equals(expected.getDescription(), command.description());
    }
}