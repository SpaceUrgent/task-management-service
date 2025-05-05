package com.task.management.persistence.jpa.project;

import com.task.management.application.project.ProjectConstants;
import com.task.management.application.project.projection.ProjectPreview;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.domain.project.model.objectvalue.TaskStatus;
import com.task.management.persistence.jpa.InvalidTestSetupException;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.AvailableTaskStatus;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@PersistenceTest
class JpaProjectRepositoryAdapterTest {
    @Autowired
    private UserEntityDao userEntityDao;
    @Autowired
    private ProjectEntityDao projectEntityDao;
    @Autowired
    private JpaProjectRepositoryAdapter projectRepositoryAdapter;

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_users.sql"
    )
    @Test
    void save_shouldReturnSavedProject() {
        final var userEntity = getFirstUserEntity();
        final var givenProject = Project.builder()
                .createdAt(Instant.now())
                .title("Test")
                .description("Description")
                .ownerId(new UserId(userEntity.getId()))
                .availableTaskStatuses(ProjectConstants.DEFAULT_TASK_STATUSES)
                .build();
        final var added = projectRepositoryAdapter.save(givenProject);
        assertMatches(givenProject, added);
        final var projectEntity = findProjectEntityOrNull(added.getId());
        assertNotNull(projectEntity);
        assertHasTaskNumberSequence(projectEntity);
        assertMatches(projectEntity, added);
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {
                    "classpath:sql/insert_project.sql",
                    "classpath:sql/insert_users.sql"
            }
    )
    @Test
    void save_shouldReturnUpdatedProject() {
        final var projectEntity = getFirstProjectEntity();
        Hibernate.initialize(projectEntity);
        final var userEntities = userEntityDao.findAll();
        final var newOwnerUserEntity = userEntities.stream()
                .filter(entity -> !projectEntity.getOwner().equals(entity))
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 not project owner user is expected in DB for test"));
        final var givenProject = Project.builder()
                .id(new ProjectId(projectEntity.getId()))
                .createdAt(projectEntity.getCreatedAt())
                .title("Project updated title")
                .description("Project updated description")
                .ownerId(new UserId(newOwnerUserEntity.getId()))
                .availableTaskStatuses(ProjectConstants.DEFAULT_TASK_STATUSES)
                .build();
        final var updatedProject = projectRepositoryAdapter.save(givenProject);
        assertMatches(givenProject, updatedProject);
        final var updateProjectEntity = projectEntityDao.findById(projectEntity.getId()).orElseThrow();
        assertMatches(updateProjectEntity, updatedProject);
        assertEquals(projectEntity.getTaskNumberSequence(), updateProjectEntity.getTaskNumberSequence());
        assertTrue(updateProjectEntity.getMembers().containsAll(projectEntity.getMembers()));
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_project.sql"
    )
    @Test
    void findById_shouldReturnOptionalOfProject_whenProjectExists() {
        final var projectEntity = getFirstProjectEntity();
        final var givenProjectId = new ProjectId(projectEntity.getId());
        final var result = projectRepositoryAdapter.find(givenProjectId).orElse(null);
        assertNotNull(result);
        assertMatches(projectEntity, result);
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_project.sql"
    )
    @Test
    void findById_shouldReturnEmptyOptional_whenProjectDoesNotExist() {
        final var givenProjectId = new ProjectId(new Random().nextLong());
        assertTrue(projectRepositoryAdapter.find(givenProjectId).isEmpty());
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_user_with_projects.sql"
    )
    @Test
    void findProjectsByMember_shouldReturnProjectList() {
        final var userEntity = getUserEntityByEmail("member@mail.com");
        final var memberProjectEntities = projectEntityDao.findAll().stream()
                .filter(projectEntity -> projectEntity.getMembers().stream().anyMatch(member -> Objects.equals(member.getUser(), userEntity)))
                .toList();
        final var givenMemberId = new UserId(userEntity.getId());
        final var result = projectRepositoryAdapter.findProjectsByMember(givenMemberId);
        assertMatches(memberProjectEntities, result);
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_project.sql"
    )
    @Test
    void findAvailableTaskStatuses_shouldReturn() {
        final var projectEntity = getFirstProjectEntity();
        final var expected = projectEntity.getAvailableTaskStatuses();
        final var taskStatuses = projectRepositoryAdapter.findAvailableTaskStatuses(new ProjectId(projectEntity.getId()));
        assertEquals(expected.size(), taskStatuses.size());
        IntStream.range(0, expected.size()).forEach(index -> {
            assertMatches(expected.get(index), taskStatuses.get(index));
        });
    }

    private UserEntity getFirstUserEntity() {
        return userEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 user is expected in DB"));
    }

    private ProjectEntity findProjectEntityOrNull(ProjectId id) {
        return projectEntityDao.findById(id.value()).orElse(null);
    }

    private ProjectEntity getFirstProjectEntity() {
        return projectEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new InvalidTestSetupException("At least 1 project is expected int DB for test"));
    }

    private UserEntity getUserEntityByEmail(String email) {
        return userEntityDao.findByEmail(email)
                .orElseThrow(() -> new InvalidTestSetupException("Test user with email '%s' is missing".formatted(email)));
    }

    private static void assertHasTaskNumberSequence(final ProjectEntity projectEntity) {
        final var taskNumberSequence = projectEntity.getTaskNumberSequence();
        assertNotNull(taskNumberSequence);
        assertEquals(projectEntity.getId(), taskNumberSequence.getId());
        assertEquals(0L, taskNumberSequence.getCurrentValue());
    }

    private static void assertMatches(Project expected, Project actual) {
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwnerId(), actual.getOwnerId());
        assertEquals(expected.getAvailableTaskStatuses(), actual.getAvailableTaskStatuses());
    }

    private static void assertMatches(ProjectEntity expected, Project actual) {
        assertEquals(expected.getId(), actual.getId().value());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwner().getId().getMemberId(), actual.getOwnerId().value());
        IntStream.range(0, expected.getAvailableTaskStatuses().size()).forEach(index -> {
            assertMatches(expected.getAvailableTaskStatuses().get(index), actual.getAvailableTaskStatuses().get(index));
        });
    }

    private static void assertMatches(AvailableTaskStatus expected, TaskStatus actual) {
        assertEquals(expected.getName(), actual.name());
        assertEquals(expected.getPosition(), actual.position());
    }

    private static void assertMatches(List<ProjectEntity> expected, List<ProjectPreview> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private static void assertMatches(ProjectEntity expected, ProjectPreview actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getOwner().getId().getMemberId(), actual.owner().id().value());
    }
}