package com.task.management.persistence.jpa.repository;

import com.task.management.application.shared.query.Sort;
import com.task.management.application.project.projection.TaskChangeLogView;
import com.task.management.application.project.projection.TaskCommentView;
import com.task.management.application.project.projection.TaskDetails;
import com.task.management.application.project.projection.TaskPreview;
import com.task.management.application.project.query.FindTasksQuery;
import com.task.management.domain.shared.model.objectvalue.*;
import com.task.management.domain.project.model.*;
import com.task.management.domain.project.model.objectvalue.*;
import com.task.management.persistence.jpa.InvalidTestSetupException;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.TaskChangeLogEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@PersistenceTest
class JpaTaskRepositoryTest {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserEntityDao userEntityDao;
    @Autowired
    private ProjectEntityDao projectEntityDao;
    @Autowired
    private TaskEntityDao taskEntityDao;
    @Autowired
    private TaskChangeLogEntityDao taskChangeLogEntityDao;
    @Autowired
    private JpaTaskRepositoryAdapter taskRepository;

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {
                    "classpath:sql/insert_project.sql",
                    "classpath:sql/insert_users.sql"
            }
    )
    @Test
    void save_shouldReturnSavedTask() {
        final var projectEntity = getFirstProjectEntity();
        final var ownerId = new UserId(projectEntity.getOwner().getId().getMemberId());
        final var givenTask = Task.builder()
                .createdAt(Instant.now())
                .dueDate(LocalDate.now().plusWeeks(1))
                .title("New task title")
                .description("New task description")
                .status("To do")
                .project(new ProjectId(projectEntity.getId()))
                .priority(TaskPriority.MEDIUM)
                .owner(ownerId)
                .assignee(ownerId)
                .build();
        final var added = taskRepository.save(givenTask);
        assertNotNull(added.getId());
        assertNotNull(added.getNumber());
        assertMatches(givenTask, added);
        final var taskEntity = taskEntityDao.findById(added.getId().value()).orElseThrow();
        assertMatches(added, taskEntity);
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    @Test
    void save_shouldReturnUpdatedTask() {
        final var taskEntity = getFirstTaskEntity();
        final var newAssignee = userEntityDao.findAll().stream()
                .filter(entity -> !taskEntity.getAssignee().equals(entity))
                .findFirst()
                .map(UserEntity::getId)
                .map(UserId::new)
                .orElseThrow(() -> new InvalidTestSetupException("New assignee user entity is expected in DB for test"));
        final var givenTask = Task.builder()
                .id(new TaskId(taskEntity.getId()))
                .createdAt(taskEntity.getCreatedAt())
                .dueDate(LocalDate.now().plusYears(1))
                .number(new TaskNumber(taskEntity.getNumber()))
                .title("Updated task title")
                .description("Update task description")
                .status("Done")
                .priority(TaskPriority.HIGHEST)
                .project(new ProjectId(taskEntity.getProject().getId()))
                .owner(new UserId(taskEntity.getOwner().getId()))
                .assignee(newAssignee)
                .build();
        final var updated = taskRepository.save(givenTask);
        assertMatches(givenTask, updated);
        final var updateTaskEntity = taskEntityDao.findById(taskEntity.getId()).orElseThrow();
        assertMatches(updated, updateTaskEntity);
    }

    @Test
    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    void saveTaskChangeLog() {
        final var taskEntity = getFirstTaskEntity();
        taskEntity.getAssignee();
        final var changeLog = TaskChangeLog.builder()
                .time(Instant.now())
                .taskId(new TaskId(taskEntity.getId()))
                .actorId(new UserId(taskEntity.getAssignee().getId()))
                .targetProperty(TaskProperty.TITLE)
                .initialValue("Initial title")
                .newValue("New value")
                .build();
        taskRepository.save(changeLog);
        final var changeLogEntities = taskChangeLogEntityDao.findAll();
        assertFalse(changeLogEntities.isEmpty());
        final var changeLogEntity = changeLogEntities.getLast();
        assertEquals(changeLog.time(), changeLogEntity.getOccurredAt());
        assertEquals(changeLog.taskId().value(), changeLogEntity.getTask().getId());
        assertEquals(changeLog.actorId().value(), changeLogEntity.getActor().getId());
        assertEquals(changeLog.targetProperty(), changeLogEntity.getTaskProperty());
        assertEquals(changeLog.initialValue(), changeLogEntity.getOldValue());
        assertEquals(changeLog.newValue(), changeLogEntity.getNewValue());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    @Test
    void findById_shouldReturnOptionalOfTask_whenTaskExists() {
        final var taskEntity = getFirstTaskEntity();
        final var givenTaskId = new TaskId(taskEntity.getId());
        final var foundOptional = taskRepository.find(givenTaskId);
        assertTrue(foundOptional.isPresent());
        assertMatches(taskEntity, foundOptional.get());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenTaskDoesNotExists() {
        final var givenTaskId = new TaskId(new Random().nextLong());
        assertTrue(taskRepository.find(givenTaskId).isEmpty());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    @Test
    void findTaskDetailsById_shouldReturnOptionalOfTaskDetails_whenTaskExists() {
        final var taskEntity = getFirstTaskEntity();
        final var givenTaskId = new TaskId(taskEntity.getId());
        final var foundOptional = taskRepository.findTaskDetails(givenTaskId);
        assertTrue(foundOptional.isPresent());
        assertMatches(taskEntity, foundOptional.get());
    }

    @Test
    void findTaskDetailsById_shouldReturnEmptyOptional_whenTaskDoesNotExists() {
        final var givenTaskId = new TaskId(new Random().nextLong());
        assertTrue(taskRepository.findTaskDetails(givenTaskId).isEmpty());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void findProjectTasks_shouldReturnTaskPreviewPageWithAllTasks_whenNoFiltersPassed() {
        final var projectId = getFirstProjectEntity().getId();
        final var expectedTaskEntities = taskEntityDao.findAll().stream()
                .filter(entity -> Objects.equals(projectId, entity.getProject().getId()))
                .toList();
        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;
        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindTasksQuery.builder()
                    .projectId(new ProjectId(projectId))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = taskRepository.findProjectTasks(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(Math.ceilDiv(expectedTaskEntities.size(), givenQuery.getPageSize()), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void findProjectTasks_shouldReturnTaskSortedPreviewPageWithAllTasks_whenSortByPassed() {
        final var projectId = getFirstProjectEntity().getId();
        final var expectedTaskEntities = taskEntityDao.findAll().stream()
                .filter(entity -> Objects.equals(projectId, entity.getProject().getId()))
                .sorted(Comparator.comparing(TaskEntity::getTitle).reversed())
                .toList();
        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;
        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = new FindTasksQueryWithSortByTitleBuilder()
                    .sortByTitle(Sort.Direction.DESC)
                    .projectId(new ProjectId(projectId))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = taskRepository.findProjectTasks(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(Math.ceilDiv(expectedTaskEntities.size(), givenQuery.getPageSize()), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void findProjectTasks_shouldReturnFilteredTaskPreviewPage_whenFiltersByAssigneeAndStatusPassed() {
        final var projectId = getFirstProjectEntity().getId();
        final var givenAssigneeId = new UserId(getUserEntityByEmail("jsnow@mail.com").getId());
        final var givenStatuses = Set.of("To Do", "In progress");

        final var expectedTaskEntities = taskEntityDao.findAll().stream()
                .filter(entity -> Objects.equals(projectId, entity.getProject().getId()))
                .filter(entity -> Objects.nonNull(entity.getAssignee()))
                .filter(entity -> Objects.equals(givenAssigneeId.value(), entity.getAssignee().getId()))
                .filter(entity -> givenStatuses.contains(entity.getStatus().getName()))
                .toList();
        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;
        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindTasksQuery.builder()
                    .projectId(new ProjectId(projectId))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .statusIn(givenStatuses)
                    .assignees(Set.of(givenAssigneeId))
                    .includeUnassigned(false)
                    .build();
            final var resultPage = taskRepository.findProjectTasks(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(Math.ceilDiv(expectedTaskEntities.size(), givenQuery.getPageSize()), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void findProjectTasks_shouldReturnFilteredTaskPreviewPage_whenFilterUnassignedPassed() {
        final var projectId = getFirstProjectEntity().getId();
        final var givenStatuses = Set.of("To Do", "In progress");

        final var expectedTaskEntities = taskEntityDao.findAll().stream()
                .filter(entity -> Objects.equals(projectId, entity.getProject().getId()))
                .filter(entity -> Objects.isNull(entity.getAssignee()))
                .filter(entity -> givenStatuses.contains(entity.getStatus().getName()))
                .toList();
        final var pageSize = 5;
        final var totalPages = expectedTaskEntities.size() / pageSize;
        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindTasksQuery.builder()
                    .projectId(new ProjectId(projectId))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .includeUnassigned(true)
                    .build();
            final var resultPage = taskRepository.findProjectTasks(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(expectedTaskEntities.size(), resultPage.total());
            assertEquals(expectedTaskEntities.size() / givenQuery.getPageSize(), resultPage.totalPages());
            final var expected = slice(expectedTaskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void projectTaskWithStatusExists_shouldReturnTrue() {
        final var taskEntity = getFirstTaskEntity();
        assertTrue(taskRepository.projectTaskWithStatusExists(new ProjectId(taskEntity.getProject().getId()), taskEntity.getStatus().getName()));
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void projectTaskWithStatusExists_shouldReturnFalse() {
        final var taskEntity = getFirstTaskEntity();
        assertFalse(taskRepository.projectTaskWithStatusExists(new ProjectId(taskEntity.getProject().getId()), "Non-existing status"));
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void unassignTasksFrom() {
        final var taskEntity = getFirstTaskEntity();
        final var assigneeId = taskEntity.getAssignee().getId();
        final var projectId = taskEntity.getProject().getId();
        taskRepository.unassignTasksFrom(new UserId(assigneeId), new ProjectId(projectId));
        entityManager.flush();
        entityManager.clear();
        final var assigneeRemovedFromAllProjectTasks = taskEntityDao.findAll().stream()
                .filter(task -> Objects.equals(task.getProject().getId(), projectId))
                .filter(task -> Objects.nonNull(task.getAssignee()))
                .noneMatch(task ->
                    Objects.equals(assigneeId, task.getAssignee().getId()) && Objects.equals(projectId, task.getProject().getId())
                );
        assertTrue(assigneeRemovedFromAllProjectTasks);
    }

    private ProjectEntity getFirstProjectEntity() {
        return projectEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 project is expected in DB for test"));
    }

    private TaskEntity getFirstTaskEntity() {
        return taskEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 task is expected in DB for test"));
    }

    private UserEntity getUserEntityByEmail(String email) {
        return userEntityDao.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Expected test user is missing"));
    }

    private void assertMatches(TaskEntity expected, TaskDetails actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getCreatedAt(), actual.createdAt());
        assertEquals(expected.getDueDate(), actual.dueDate());
        assertEquals(expected.getNumber(), actual.number().value());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertEquals(expected.getStatusName(), actual.status());
        assertEquals(expected.getProject().getId(), actual.projectId().value());
        assertEquals(expected.getOwner().getId(), actual.owner().id().value());
        assertEquals(expected.getAssignee().getId(), actual.assignee().id().value());
        final var expectedChangeLogs = expected.getChangeLogs();
        final var actualChangeLogs = actual.changeLogs();
        IntStream.range(0, expectedChangeLogs.size()).forEach(index -> {
            assertMatches(expectedChangeLogs.get(index), actualChangeLogs.get(index));
        });
        final var expectedComments = expected.getComments();
        final var actualComments = actual.comments();
        IntStream.range(0, expectedChangeLogs.size()).forEach(index -> {
            assertMatches(expectedComments.get(index), actualComments.get(index));
        });
    }

    private void assertMatches(Task expected, Task actual) {
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getDueDate(), actual.getDueDate());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getPriority(), actual.getPriority());
        assertEquals(expected.getOwner(), actual.getOwner());
        assertEquals(expected.getAssignee(), actual.getAssignee());
    }

    private void assertMatches(TaskEntity expected, Task actual) {
        assertMatches(actual, expected);
    }

    private void assertMatches(Task expected, TaskEntity actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getDueDate(), actual.getDueDate());
        assertEquals(expected.getNumber().value(), actual.getNumber());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatusName());
        assertEquals(expected.getPriority().order(), actual.getPriority());
        assertEquals(expected.getOwner().value(), actual.getOwner().getId());
        assertEquals(expected.getAssignee().value(), actual.getAssignee().getId());
    }

    private static <T> List<T> slice (List<T> target, int page, int size) {
        return IntStream.range(page * size, (page * size) + size)
                .mapToObj(target::get)
                .toList();
    }

    private void assertMatches(List<TaskEntity> expected, List<TaskPreview> actual) {
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private void assertMatches(TaskEntity expected, TaskPreview actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getCreatedAt(), actual.createdAt());
        assertEquals(expected.getDueDate(), actual.dueDate());
        assertEquals(expected.getNumber(), actual.number().value());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getStatusName(), actual.status());
        Optional.ofNullable(expected.getAssignee()).ifPresentOrElse(
                expectedAssignee -> assertEquals(expectedAssignee.getId(), actual.assignee().id().value()),
                () -> assertNull(actual.assignee())
        );
    }

    private void assertMatches(TaskChangeLogEntity expected, TaskChangeLogView actual) {
        assertEquals(expected.getOccurredAt(), actual.time());
        assertEquals(expected.getActor().getId(), actual.actor().id().value());
        assertEquals(expected.getTaskProperty(), actual.targetProperty());
        assertEquals(expected.getOldValue(), actual.initialValue());
        assertEquals(expected.getNewValue(), actual.newValue());
    }

    private void assertMatches(TaskCommentEntity expected, TaskCommentView actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getCreatedAt(), actual.createdAt());
        assertEquals(expected.getAuthor().getId(), actual.author().id().value());
        assertEquals(expected.getContent(), actual.content());
    }

    public static class FindTasksQueryWithSortByTitleBuilder extends FindTasksQuery.Builder {

        public FindTasksQuery.Builder sortByTitle(Sort.Direction direction) {
            return this.sortBy("title", direction);
        }
    }
}