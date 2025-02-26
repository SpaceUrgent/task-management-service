package com.task.management.persistence.jpa;

import com.task.management.application.project.model.Project;
import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.ProjectPreview;
import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@Transactional
@SpringBootTest(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
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
    void add_shouldReturnSavedProject() {
        final var owner = userEntityDao.findAll().stream()
                .findFirst()
                .map(UserEntity::getId)
                .map(ProjectUserId::new)
                .map(ProjectUser::withId)
                .orElseThrow(() -> new IllegalStateException("At least 1 user is expected"));
        final var givenProject = Project.builder()
                .createdAt(Instant.now())
                .title("Test")
                .description("Description")
                .owner(owner)
                .build();
        final var added = projectRepositoryAdapter.add(givenProject);
        assertMatches(givenProject, added);
        final var projectEntity = projectEntityDao.findById(added.getId().value()).orElse(null);
        assertNotNull(projectEntity);
        assertMatches(projectEntity, added);
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = "classpath:sql/insert_project.sql"
    )
    @Test
    void findById_shouldReturnOptionalOfProject_whenProjectExists() {
        final var projectEntity = projectEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Project in DB is required for test"));
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
        final var memberEntity = userEntityDao.findByEmail("member@mail.com")
                .orElseThrow(() -> new IllegalStateException("Test user is missing"));
        final var memberProjectEntities = projectEntityDao.findAll().stream()
                .filter(projectEntity -> projectEntity.getMembers().contains(memberEntity))
                .toList();
        final var givenMemberId = new ProjectUserId(memberEntity.getId());
        final var result = projectRepositoryAdapter.findProjectsByMember(givenMemberId);
        assertMatches(memberProjectEntities, result);
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {
                    "classpath:sql/insert_project.sql",
                    "classpath:sql/insert_users.sql"
            }
    )
    @Test
    void addMember_shouldAddNewMemberToProject() {
        final var projectEntity = projectEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 project is expected int DB for test"));
        final var userEntities = userEntityDao.findAll();
        final var newMemberUserEntity = userEntities.stream()
                .filter(entity -> !projectEntity.getMembers().contains(entity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 not project member user is expected in DB for test"));
        final var givenProjectId = new ProjectId(projectEntity.getId());
        final var givenMemberId = new ProjectUserId(newMemberUserEntity.getId());
        projectRepositoryAdapter.addMember(givenProjectId, givenMemberId);
        final var updatedProjectEntity = projectEntityDao.findById(projectEntity.getId()).orElseThrow();
        assertTrue(updatedProjectEntity.getMembers().contains(newMemberUserEntity));
    }

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {
                    "classpath:sql/insert_project.sql",
                    "classpath:sql/insert_users.sql"
            }
    )
    @Test
    void update_shouldReturnUpdatedProject() {
        final var projectEntity = projectEntityDao.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 project is expected int DB for test"));
        final var userEntities = userEntityDao.findAll();
        final var newOwnerUserEntity = userEntities.stream()
                .filter(entity -> !projectEntity.getOwner().equals(entity))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least 1 not project member user is expected in DB for test"));
        final var givenProject = Project.builder()
                .id(new ProjectId(projectEntity.getId()))
                .createdAt(projectEntity.getCreatedAt())
                .title("Project updated title")
                .description("Project updated description")
                .owner(ProjectUser.withId(new ProjectUserId(newOwnerUserEntity.getId())))
                .build();
        final var updatedProject = projectRepositoryAdapter.update(givenProject);
        assertMatches(givenProject, updatedProject);
        final var updateProjectEntity = projectEntityDao.findById(projectEntity.getId()).orElseThrow();
        assertMatches(updateProjectEntity, updatedProject);
    }

    private void assertMatches(Project expected, Project actual) {
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwner().id(), actual.getOwner().id());
    }

    private void assertMatches(ProjectEntity expected, Project actual) {
        assertEquals(expected.getId(), actual.getId().value());
        assertEquals(expected.getCreatedAt(), actual.getCreatedAt());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwner().getId(), actual.getOwner().id().value());
    }

    private void assertMatches(List<ProjectEntity> expected, List<ProjectPreview> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertMatches(expected.get(i), actual.get(i));
        }
    }

    private void assertMatches(ProjectEntity expected, ProjectPreview actual) {
        assertEquals(expected.getId(), actual.id().value());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getOwner().getId(), actual.owner().id().value());
    }
}