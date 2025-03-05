package com.task.managment.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.AssignTaskUseCase;
import com.task.management.application.project.port.in.GetTaskDetailsUseCase;
import com.task.management.application.project.port.in.UpdateTaskStatusUseCase;
import com.task.management.application.project.port.in.UpdateTaskUseCase;
import com.task.management.application.project.port.in.command.UpdateTaskCommand;
import com.task.managment.web.WebTest;
import com.task.managment.web.dto.request.AssignTaskRequest;
import com.task.managment.web.dto.request.UpdateTaskRequest;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static com.task.managment.web.TestUtils.PROJECT_USER_ID;
import static com.task.managment.web.TestUtils.randomLong;
import static com.task.managment.web.TestUtils.randomProjectId;
import static com.task.managment.web.TestUtils.randomProjectUser;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest(controllerClass = TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AssignTaskUseCase assignTaskUseCase;
    @MockBean
    private GetTaskDetailsUseCase getTaskDetailsUseCase;
    @MockBean
    private UpdateTaskUseCase updateTaskUseCase;
    @MockBean
    private UpdateTaskStatusUseCase updateTaskStatusUseCase;

    @MockUser
    @Test
    void getTaskDetails() throws Exception {
        final var taskDetails = randomTaskDetails();
        final var taskOwner = taskDetails.owner();
        final var assignee = taskDetails.assignee();
        doReturn(taskDetails).when(getTaskDetailsUseCase).getTaskDetails(eq(PROJECT_USER_ID), eq(taskDetails.id()));
        mockMvc.perform(get("/api/tasks/{taskId}", taskDetails.id().value()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(taskDetails.id().value()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.projectId").value(taskDetails.projectId().value()))
                .andExpect(jsonPath("$.title").value(taskDetails.title()))
                .andExpect(jsonPath("$.description").value(taskDetails.description()))
                .andExpect(jsonPath("$.status").value(taskDetails.status().value()))
                .andExpect(jsonPath("$.owner.id").value(taskOwner.id().value()))
                .andExpect(jsonPath("$.owner.email").value(taskOwner.email()))
                .andExpect(jsonPath("$.owner.firstName").value(taskOwner.firstName()))
                .andExpect(jsonPath("$.owner.lastName").value(taskOwner.lastName()))
                .andExpect(jsonPath("$.assignee.id").value(assignee.id().value()))
                .andExpect(jsonPath("$.assignee.email").value(assignee.email()))
                .andExpect(jsonPath("$.assignee.firstName").value(assignee.firstName()))
                .andExpect(jsonPath("$.assignee.lastName").value(assignee.lastName()));

    }

    @MockUser
    @Test
    void updateTask() throws Exception {
        final var givenTaskId = randomTaskId();
        final var request = getUpdateTaskRequest();
        final var expectedCommand = UpdateTaskCommand.builder()
                .taskId(givenTaskId)
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
        mockMvc.perform(put("/api/tasks/{taskId}", givenTaskId.value())
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
        verify(updateTaskUseCase).updateTask(eq(PROJECT_USER_ID), eq(expectedCommand));
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
        verify(updateTaskStatusUseCase).updateStatus(eq(PROJECT_USER_ID), eq(givenTaskId), eq(new TaskStatus(request.getStatus())));
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
        verify(assignTaskUseCase)
                .assignTask(eq(PROJECT_USER_ID), eq(givenTaskId), eq(new ProjectUserId(givenRequest.getAssigneeId())));
    }

    private UpdateTaskRequest getUpdateTaskRequest() {
        final var request = new UpdateTaskRequest();
        request.setTitle("Updated title");
        request.setDescription("Updated description");
        return request;
    }

    private AssignTaskRequest getAssignTaskRequest() {
        final var request = new AssignTaskRequest();
        request.setAssigneeId(randomLong());
        return request;
    }

    private UpdateTaskStatusRequest getUpdateTaskStatusRequest() {
        final var request = new UpdateTaskStatusRequest();
        request.setStatus("DONE");
        return request;
    }

    private TaskDetails randomTaskDetails() {
        return TaskDetails.builder()
                .id(randomTaskId())
                .createdAt(Instant.now().minus(Duration.ofDays(1)))
                .projectId(randomProjectId())
                .title("Task title")
                .description("Task description")
                .status(TaskStatus.IN_PROGRESS)
                .assignee(randomProjectUser())
                .owner(randomProjectUser())
                .build();
    }

    private static TaskId randomTaskId() {
        return new TaskId(randomLong());
    }
}