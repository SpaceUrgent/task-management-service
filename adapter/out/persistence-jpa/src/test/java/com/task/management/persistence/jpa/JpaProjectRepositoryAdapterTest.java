package com.task.management.persistence.jpa;

import com.task.management.application.common.PageQuery;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.out.UpdateProjectPort.UpdateProjectCommand;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.repository.JpaProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static java.lang.Math.ceilDiv;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(classes = JpaTestConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class JpaProjectRepositoryAdapterTest {

    @Autowired
    private JpaUserRepositoryAdapter userRepository;
    @Autowired
    private JpaProjectRepositoryAdapter projectRepository;
    @Autowired
    private JpaProjectRepository jpaProjectRepository;

    @Test
    void add_shouldReturnSavedProject() {
        var owner = userRepository.add(getTestUser());
        final var givenProject = getTestProject(owner.getId());
        final var added = projectRepository.add(givenProject);
        assertMatches(givenProject, added);
        final var savedJpaUser = jpaProjectRepository.findById(added.getId().value()).orElseThrow();
        assertMatches(added, savedJpaUser);
        assertNotNull(savedJpaUser.getCreatedAt());
    }

    @Test
    void findById_shouldReturnOptionalOfProject_whenProjectExists() {
        final var expectedProject = saveAndGetTestProject();
        assertEquals(expectedProject, projectRepository.findById(expectedProject.getId()).orElse(null));
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenProjectDoesNotExists() {
        final var givenProjectId = randomProjectId();
        assertTrue(projectRepository.findById(givenProjectId).isEmpty());
    }

    @Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = "classpath:sql/insert_projects.sql"
    )
    @Test
    void findProjectsByMember_shouldReturnProjects() {
        final var totalProjectsWithTestMember = 17;
        final var givenMemberId = userRepository.findByEmail("member@mail.com")
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("Test project member is expected in DB"));
        assertEquals(17, countProjectEntitiesByMemberId(givenMemberId.value()), "Invalid test setup, expected 17 projects in DB");
        int givenPageNumber = 1;
        int givenPageSize = 10;
        PageQuery givenPage;
        List<Project> projects;
        int receivedTotal = 0;
        for (int i = 0; i < ceilDiv(17, givenPageSize); i++) {
            givenPage = new PageQuery(givenPageNumber, givenPageSize);
            projects = projectRepository.findProjectsByMember(givenMemberId, givenPage);
            assertTrue(projects.stream().allMatch(project -> project.hasMember(givenMemberId)), "Every project must have given member");
            givenPageNumber++;
            receivedTotal += projects.size();
        }
        assertEquals(totalProjectsWithTestMember, receivedTotal);
    }

    @Test
    void findProjectDetails_shouldReturnOptionalOfProjectDetails_whenProjectExists() {
        final var expectedOwner = saveAndGetTestUser();
        final var expectedProject = saveAndGetTestProject(expectedOwner);
        final var projectDetails = projectRepository.findProjectDetails(expectedProject.getId()).orElse(null);
        assertNotNull(projectDetails);
        assertEquals(expectedProject, projectDetails.project());
        assertEquals(expectedOwner, projectDetails.owner());
        assertEquals(List.of(expectedOwner), projectDetails.members());
    }

    @Test
    void findProjectDetails_shouldReturnEmptyOptional_whenProjectDoesNotExist() {
        assertTrue(projectRepository.findProjectDetails(randomProjectId()).isEmpty());
    }

    @Test
    void updateProject_shouldReturnUpdate_whenAllConditionsMet() {
        final var project = saveAndGetTestProject();
        final var givenCommand = new UpdateProjectCommand("Update title", "Updated description");
        final var updated = projectRepository.update(project.getId(), givenCommand);
        assertEquals(project.getId(), updated.getId());
        assertEquals(givenCommand.title(), updated.getTitle());
        assertEquals(givenCommand.description(), updated.getDescription());
        assertEquals(project.getOwner(), updated.getOwner());
        assertEquals(project.getMembers(), updated.getMembers());
    }

    @Test
    void updateProject_shouldThrowEntityNotFoundException_whenProjectEntityDoesNotExist() {
        final var givenCommand = new UpdateProjectCommand("Update title", "Updated description");
        assertThrows(
                EntityNotFoundException.class,
                () -> projectRepository.update(randomProjectId(), givenCommand)
        );
    }

    @Test
    void addMember_shouldAddProjectMember() {
        final var givenProjectId = saveAndGetTestProject().getId();
        final var givenMemberId = saveAndGetTestUser().getId();
        projectRepository.addMember(givenProjectId, givenMemberId);
        final var updatedProject = projectRepository.findById(givenProjectId).orElse(null);
        assertNotNull(updatedProject);
        assertTrue(updatedProject.hasMember(givenMemberId));
    }

    @Test
    void addMember_shouldThrowEntityNotFoundException_whenProjectEntityDoesNotExist() {
        final var givenProjectId = randomProjectId();
        final var givenMemberId = saveAndGetTestUser().getId();
        assertThrows(
                EntityNotFoundException.class,
                () -> projectRepository.addMember(givenProjectId, givenMemberId)
        );
    }

    private void assertMatches(Project expected, ProjectEntity actual) {
        assertEquals(expected.getId().value(), actual.getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwner().id().value(), actual.getOwner().getId());
    }

    private void assertMatches(Project expected, Project actual) {
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getOwner().id(), actual.getOwner().id());
    }

    private long countProjectEntitiesByMemberId(Long memberId) {
        return jpaProjectRepository.findAll().stream()
                .filter(containsMemberWithIdPredicate(memberId))
                .count();
    }

    private User saveAndGetTestUser() {
        return userRepository.add(getTestUser());
    }

    private Project saveAndGetTestProject() {
        return saveAndGetTestProject(saveAndGetTestUser());
    }

    private Project saveAndGetTestProject(final User owner) {
        return projectRepository.add(getTestProject(owner.getId()));
    }

    private static Predicate<ProjectEntity> containsMemberWithIdPredicate(Long memberId) {
        return projectEntity -> projectEntity.getMembers().stream().anyMatch(userEntity -> memberId.equals(userEntity.getId()));
    }

    private static Project getTestProject(UserId ownerId) {
        final var randomLong = randomLong();
        final var owner = ProjectUser.withId(ownerId);
        return Project.builder()
                .title("Project %d".formatted(randomLong))
                .description("Project %d description".formatted(randomLong))
                .owner(owner)
                .members(Set.of(owner))
                .build();
    }

    private static User getTestUser() {
        return User.builder()
                .email("test%d@domain.com".formatted(randomLong()))
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .encryptedPassword("encryptedPassword")
                .build();
    }

    private static ProjectId randomProjectId() {
        return new ProjectId(randomLong());
    }

    private static long randomLong() {
        return new Random().nextLong();
    }

}