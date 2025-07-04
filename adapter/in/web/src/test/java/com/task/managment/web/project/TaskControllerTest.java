package com.task.managment.web.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.project.command.UpdateTaskCommand;
import com.task.management.application.project.port.in.TaskUseCase;
import com.task.management.application.project.projection.TaskChangeLogView;
import com.task.management.application.project.projection.TaskCommentView;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.domain.shared.model.objectvalue.TaskId;
import com.task.management.domain.shared.model.objectvalue.TaskNumber;
import com.task.management.domain.shared.model.objectvalue.TaskPriority;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.*;
import com.task.managment.web.WebTest;
import com.task.managment.web.project.dto.TaskChangeLogDto;
import com.task.managment.web.project.dto.TaskCommentDto;
import com.task.managment.web.project.dto.TaskDetailsDto;
import com.task.managment.web.project.dto.request.*;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static com.task.managment.web.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = {
        "com.task.managment.web.shared.mapper",
        "com.task.managment.web.project.mapper"
})
@WebTest(testClasses = TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TaskUseCase taskUseCase;

    @MockUser
    @Test
    void getTaskDetails() throws Exception {
        final var taskDetails = randomTaskDetails();
        final var taskOwner = taskDetails.owner();
        final var assignee = taskDetails.assignee();
        doReturn(taskDetails).when(taskUseCase).getTaskDetails(eq(USER_ID), eq(taskDetails.id()));
        final var responseBody = mockMvc.perform(get("/api/tasks/{taskId}", taskDetails.id().value()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskDetails.id().value()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.dueDate").value(taskDetails.dueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                .andExpect(jsonPath("$.number").value(taskDetails.number().value()))
                .andExpect(jsonPath("$.projectId").value(taskDetails.projectId().value()))
                .andExpect(jsonPath("$.title").value(taskDetails.title()))
                .andExpect(jsonPath("$.description").value(taskDetails.description()))
                .andExpect(jsonPath("$.status").value(taskDetails.status()))
                .andExpect(jsonPath("$.priority").value(taskDetails.priority().priorityName()))
                .andExpect(jsonPath("$.owner.id").value(taskOwner.id().value()))
                .andExpect(jsonPath("$.owner.email").value(taskOwner.email().value()))
                .andExpect(jsonPath("$.owner.firstName").value(taskOwner.firstName()))
                .andExpect(jsonPath("$.owner.lastName").value(taskOwner.lastName()))
                .andExpect(jsonPath("$.assignee.id").value(assignee.id().value()))
                .andExpect(jsonPath("$.assignee.email").value(assignee.email().value()))
                .andExpect(jsonPath("$.assignee.firstName").value(assignee.firstName()))
                .andExpect(jsonPath("$.assignee.lastName").value(assignee.lastName()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        final var actual = objectMapper.readValue(responseBody, TaskDetailsDto.class);
        assertMatches(taskDetails.changeLogs(), actual.getChangeLogs());
        final var expectedComments = taskDetails.comments();
        final var actualComments = actual.getComments();
        IntStream.range(0, expectedComments.size()).forEach(index ->
                assertMatches(expectedComments.get(index), actualComments.get(index))
        );
    }

    @MockUser
    @Test
    void updateTask() throws Exception {
        final var givenTaskId = randomTaskId();
        final var request = getUpdateTaskRequest();
        final var expectedCommand = UpdateTaskCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assigneeId(new UserId(request.getAssigneeId()))
                .taskStatus(request.getStatus())
                .priority(TaskPriority.withPriorityName(request.getPriority()))
                .dueDate(request.getDueDate())
                .build();
        mockMvc.perform(put("/api/tasks/{taskId}", givenTaskId.value())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(taskUseCase).updateTask(eq(USER_ID), eq(givenTaskId), eq(expectedCommand));
    }

    @MockUser
    @Test
    void updateTaskStatus() throws Exception {
        final var givenTaskId = randomTaskId();
        final var request = getUpdateTaskStatusRequest();
        mockMvc.perform(patch("/api/tasks/{taskId}/status", givenTaskId.value())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(taskUseCase).updateStatus(eq(USER_ID), eq(givenTaskId), eq(request.getStatus()));
    }

    @MockUser
    @Test
    void assignTask() throws Exception {
        final var givenRequest = getAssignTaskRequest();
        final var givenTaskId = randomTaskId();
        mockMvc.perform(patch("/api/tasks/{taskId}/assign", givenTaskId.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(givenRequest)))
                        .andExpect(status().isOk());
        verify(taskUseCase)
                .assignTask(eq(USER_ID), eq(givenTaskId), eq(new UserId(givenRequest.getAssigneeId())));
    }

    @MockUser
    @Test
    void updatePriority() throws Exception {
        final var givenRequest = getUpdatePriorityRequest();
        final var givenTaskId = randomTaskId();
        mockMvc.perform(patch("/api/tasks/{taskId}/priority", givenTaskId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isOk());
        verify(taskUseCase)
                .updatePriority(eq(USER_ID), eq(givenTaskId), eq(TaskPriority.withPriorityName(givenRequest.getPriority())));
    }

    @MockUser
    @Test
    void addComment() throws Exception {
        final var givenRequest = getAddCommentRequest();
        final var givenTaskId = randomTaskId();
        mockMvc.perform(post("/api/tasks/{taskId}/comments", givenTaskId.value())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isOk());
        verify(taskUseCase)
                .addComment(eq(USER_ID), eq(givenTaskId), eq(givenRequest.getComment()));
    }

    private void assertMatches(List<TaskChangeLogView> expected, List<TaskChangeLogDto> actual) {
        assertEquals(expected.size(), actual.size());
        IntStream.range(0, expected.size()).forEach(index -> assertMatches(expected.get(index), actual.get(index)));
    }

    private void assertMatches(TaskChangeLogView expected, TaskChangeLogDto actual) {
        assertEquals(expected.time(), actual.getOccurredAt());
        assertEquals("%s updated title".formatted(expected.actor().fullName()), actual.getLogMessage());
        assertEquals(expected.initialValue(), actual.getOldValue());
        assertEquals(expected.newValue(), actual.getNewValue());
    }

    private UpdateTaskRequest getUpdateTaskRequest() {
        final var request = new UpdateTaskRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        request.setAssigneeId(randomLong());
        request.setStatus("Done");
        request.setPriority(TaskPriority.HIGHEST.priorityName());
        request.setDueDate(LocalDate.now().plusYears(1));
        return request;
    }

    private AssignTaskRequest getAssignTaskRequest() {
        final var request = new AssignTaskRequest();
        request.setAssigneeId(randomLong());
        return request;
    }

    private UpdateTaskStatusRequest getUpdateTaskStatusRequest() {
        final var request = new UpdateTaskStatusRequest();
        request.setStatus("Done");
        return request;
    }

    private UpdateTaskPriorityRequest getUpdatePriorityRequest() {
        final var request = new UpdateTaskPriorityRequest();
        request.setPriority(TaskPriority.HIGHEST.priorityName());
        return request;
    }

    private static AddCommentRequest getAddCommentRequest() {
        final var request = new AddCommentRequest();
        request.setComment("Some meaningful comment");
        return request;
    }

    private TaskDetails randomTaskDetails() {
        return TaskDetails.builder()
                .id(randomTaskId())
                .createdAt(Instant.now().minus(Duration.ofDays(1)))
                .updatedAt(Instant.now())
                .dueDate(LocalDate.now().plusWeeks(1))
                .projectId(randomProjectId())
                .number(new TaskNumber(randomLong()))
                .title("Task title")
                .description("Task description")
                .status("In progress")
                .priority(TaskPriority.MEDIUM)
                .assignee(randomUserInfo())
                .owner(randomUserInfo())
                .changeLogs(changeLogViews())
                .comments(commentViews())
                .build();
    }

    private void assertMatches(TaskCommentView expected, TaskCommentDto actual) {
        assertEquals(expected.id().value(), actual.getId());
        assertEquals(expected.createdAt(), actual.getCreatedAt());
        assertEquals(expected.content(), actual.getContent());
    }

    private List<TaskChangeLogView> changeLogViews() {
        return List.of(
                TaskChangeLogView.builder()
                        .time(Instant.now())
                        .actor(randomUserInfo())
                        .targetProperty(TaskProperty.TITLE)
                        .initialValue("Old title")
                        .newValue("New value")
                        .build()
        );
    }

    private List<TaskCommentView> commentViews() {
        return List.of(
                TaskCommentView.builder()
                        .id(new TaskCommentId(randomLong()))
                        .createdAt(Instant.now())
                        .author(randomUserInfo())
                        .content("Comment value")
                        .build()
        );
    }

    private static TaskId randomTaskId() {
        return new TaskId(randomLong());
    }
}