package com.task.management.persistence.jpa;

import com.task.management.application.common.Sort;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.application.project.model.Task;
import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskPreview;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.application.project.port.in.query.FindTasksQuery;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.TaskEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.TaskEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@Transactional
@SpringBootTest(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JpaTaskRepositoryAdapterTest {
    @Autowired
    private UserEntityDao userEntityDao;
    @Autowired
    private ProjectEntityDao projectEntityDao;
    @Autowired
    private TaskEntityDao taskEntityDao;
    @Autowired
    private JpaTaskRepositoryAdapter taskRepositoryAdapter;

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {
                    "classpath:sql/insert_project.sql",
                    "classpath:sql/insert_users.sql"
            }
    )
    @Test
    void add_shouldReturnSavedTask() {
        final var projectEntity = projectEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 project is expected in DB for test"));
        final var ownerId = new ProjectUserId(projectEntity.getOwner().getId());
        final var owner = ProjectUser.withId(ownerId);
        final var givenTask = Task.builder()
                .createdAt(Instant.now())
                .title("New task title")
                .description("New task description")
                .status(TaskStatus.TO_DO)
                .project(new ProjectId(projectEntity.getId()))
                .owner(owner)
                .assignee(owner)
                .build();
        final var added = taskRepositoryAdapter.add(givenTask);
        assertNotNull(added.getId());
        assertMatches(givenTask, added);
        final var taskEntity = taskEntityDao.findById(added.getId().value()).orElseThrow();
        assertMatches(added, taskEntity);
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    @Test
    void findById_shouldReturnOptionalOfTask_whenTaskExists() {
        final var taskEntity = taskEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 task is expected in DB for test"));
        final var givenTaskId = new TaskId(taskEntity.getId());
        final var foundOptional = taskRepositoryAdapter.find(givenTaskId);
        assertTrue(foundOptional.isPresent());
        assertMatches(taskEntity, foundOptional.get());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenTaskDoesNotExists() {
        final var givenTaskId = new TaskId(new Random().nextLong());
        assertTrue(taskRepositoryAdapter.find(givenTaskId).isEmpty());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    @Test
    void findTaskDetailsById_shouldReturnOptionalOfTaskDetails_whenTaskExists() {
        final var taskEntity = taskEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 task is expected in DB for test"));
        final var givenTaskId = new TaskId(taskEntity.getId());
        final var foundOptional = taskRepositoryAdapter.findTaskDetailsById(givenTaskId);
        assertTrue(foundOptional.isPresent());
        assertMatches(taskEntity, foundOptional.get());
    }

    @Test
    void findTaskDetailsById_shouldReturnEmptyOptional_whenTaskDoesNotExists() {
        final var givenTaskId = new TaskId(new Random().nextLong());
        assertTrue(taskRepositoryAdapter.findTaskDetailsById(givenTaskId).isEmpty());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void findProjectTasks_shouldReturnTaskPreviewPageWithAllTasks_whenNoFiltersPassed() {
        final var projectId = projectEntityDao.findAll().stream()
                .findFirst()
                .map(ProjectEntity::getId)
                .orElseThrow(() -> new IllegalStateException("At least 1 task is expected in DB for test"));
        final var taskEntities = taskEntityDao.findAll().stream()
                .filter(entity -> Objects.equals(projectId, entity.getProject().getId()))
                .toList();
        final var pageSize = 5;
        final var totalPages = taskEntities.size() / pageSize;
        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindTasksQuery.builder()
                    .projectId(new ProjectId(projectId))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = taskRepositoryAdapter.findProjectTasks(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(taskEntities.size(), resultPage.total());
            assertEquals(taskEntities.size() / givenQuery.getPageSize(), resultPage.totalPages());
            final var expected = slice(taskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
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
        final var projectId = projectEntityDao.findAll().stream()
                .findFirst()
                .map(ProjectEntity::getId)
                .orElseThrow(() -> new IllegalStateException("At least 1 task is expected in DB for test"));
        final var taskEntities = taskEntityDao.findAll().stream()
                .filter(entity -> Objects.equals(projectId, entity.getProject().getId()))
                .sorted(Comparator.comparing(TaskEntity::getTitle).reversed())
                .toList();
        final var pageSize = 5;
        final var totalPages = taskEntities.size() / pageSize;
        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = new FindTasksQueryWithSortByTitleBuilder()
                    .sortByTitle(Sort.Direction.DESC)
                    .projectId(new ProjectId(projectId))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .build();
            final var resultPage = taskRepositoryAdapter.findProjectTasks(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(taskEntities.size(), resultPage.total());
            assertEquals(taskEntities.size() / givenQuery.getPageSize(), resultPage.totalPages());
            final var expected = slice(taskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_tasks.sql"
    )
    @Test
    void findProjectTasks_shouldReturnFilteredTaskPreviewPage_whenFiltersPassed() {
        final var projectId = projectEntityDao.findAll().stream()
                .findFirst()
                .map(ProjectEntity::getId)
                .orElseThrow(() -> new IllegalStateException("At least 1 task is expected in DB for test"));
        final var givenAssigneeId = userEntityDao.findByEmail("jsnow@mail.com")
                .map(UserEntity::getId)
                .map(ProjectUserId::new)
                .orElseThrow(() -> new IllegalStateException("Expected test user is missing"));
        final var givenStatuses = Set.of(TaskStatus.TO_DO, TaskStatus.IN_PROGRESS);
        final var statusValueList = givenStatuses.stream()
                .map(TaskStatus::value)
                .toList();
        final var taskEntities = taskEntityDao.findAll().stream()
                .filter(entity -> Objects.equals(projectId, entity.getProject().getId()))
                .filter(entity -> Objects.equals(givenAssigneeId.value(), entity.getAssignee().getId()))
                .filter(entity -> statusValueList.contains(entity.getStatus()))
                .toList();
        final var pageSize = 5;
        final var totalPages = taskEntities.size() / pageSize;
        int currentPage = 1;
        while (currentPage <= totalPages) {
            final var givenQuery = FindTasksQuery.builder()
                    .projectId(new ProjectId(projectId))
                    .pageNumber(currentPage)
                    .pageSize(pageSize)
                    .statusIn(givenStatuses)
                    .assigneeId(givenAssigneeId)
                    .build();
            final var resultPage = taskRepositoryAdapter.findProjectTasks(givenQuery);
            assertEquals(givenQuery.getPageNumber(), resultPage.pageNo());
            assertEquals(givenQuery.getPageSize(), resultPage.pageSize());
            assertEquals(taskEntities.size(), resultPage.total());
            assertEquals(taskEntities.size() / givenQuery.getPageSize(), resultPage.totalPages());
            final var expected = slice(taskEntities, givenQuery.getPageNumber() - 1, givenQuery.getPageSize());
            assertMatches(expected, resultPage.content());
            currentPage++;
        }
    }

    private void assertMatches(List<TaskEntity> expected, List<TaskPreview> actual) {
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private void assertMatches(TaskEntity expected, TaskPreview actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getCreatedAt(), actual.createdAt());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getStatus(), actual.status().value());
        assertEquals(expected.getAssignee().getId(), actual.assignee().id().value());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_task.sql"
    )
    @Test
    void update_shouldReturnUpdatedTask() {
        final var taskEntity = taskEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 task is expected in DB for test"));
        final var newAssignee = userEntityDao.findAll().stream()
                .filter(entity -> !taskEntity.getAssignee().equals(entity))
                .findFirst()
                .map(entity -> ProjectUser.withId(new ProjectUserId(entity.getId())))
                .orElseThrow(() -> new IllegalStateException("New assignee user entity is expected in DB for test"));
        final var givenTask = Task.builder()
                .id(new TaskId(taskEntity.getId()))
                .createdAt(taskEntity.getCreatedAt())
                .title("Updated task title")
                .description("Update task description")
                .status(TaskStatus.DONE)
                .project(new ProjectId(taskEntity.getProject().getId()))
                .owner(ProjectUser.withId(new ProjectUserId(taskEntity.getOwner().getId())))
                .assignee(newAssignee)
                .build();
        final var updated = taskRepositoryAdapter.update(givenTask);
        assertMatches(givenTask, updated);
        final var updateTaskEntity = taskEntityDao.findById(taskEntity.getId()).orElseThrow();
        assertMatches(updated, updateTaskEntity);
    }

    private void assertMatches(TaskEntity expected, TaskDetails actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getCreatedAt(), actual.createdAt());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertEquals(expected.getStatus(), actual.status().value());
        assertEquals(expected.getProject().getId(), actual.projectId().value());
        assertEquals(expected.getOwner().getId(), actual.owner().id().value());
        assertEquals(expected.getAssignee().getId(), actual.assignee().id().value());
    }

    private void assertMatches(Task expected, Task actual) {
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getOwner().id(), actual.getOwner().id());
        assertEquals(expected.getAssignee().id(), actual.getAssignee().id());
    }

    private void assertMatches(TaskEntity expected, Task actual) {
        assertMatches(actual, expected);
    }

    private void assertMatches(Task expected, TaskEntity actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getStatus().value(), actual.getStatus());
        assertEquals(expected.getOwner().id().value(), actual.getOwner().getId());
        assertEquals(expected.getAssignee().id().value(), actual.getAssignee().getId());
    }

    private static <T> List<T> slice (List<T> target, int page, int size) {
        return IntStream.range(page * size, (page * size) + size)
                .mapToObj(target::get)
                .toList();
    }

    public static class FindTasksQueryWithSortByTitleBuilder extends FindTasksQuery.Builder {
        public FindTasksQuery.Builder sortByTitle(Sort.Direction direction) {
            return this.sortBy("title", direction);
        }
    }
}