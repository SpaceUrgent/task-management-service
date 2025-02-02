package com.task.management.persistence.jpa;

import com.task.management.application.common.PageQuery;
import com.task.management.application.dto.ProjectDetailsDTO;
import com.task.management.application.dto.ProjectUserDTO;
import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.ProjectUser;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.persistence.jpa.entity.ProjectEntity;
import com.task.management.persistence.jpa.entity.UserEntity;
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
            givenPageNumber++;
            receivedTotal += projects.size();
        }
        assertEquals(totalProjectsWithTestMember, receivedTotal);
    }

    @Test
    void findProjectDetails_shouldReturnOptionalOfProjectDetails_whenProjectExists() {
        final var owner = saveAndGetTestUser();
        final var project = saveAndGetTestProject(owner);
        final var projectEntity = jpaProjectRepository.findById(project.getId().value()).orElseThrow();
        final var projectDetails = projectRepository.getProjectDetails(project.getId());
        assertMatches(projectEntity, projectDetails);
    }

    @Test
    void findProjectDetails_shouldReturnEmptyOptional_whenProjectDoesNotExist() {
        assertNull(projectRepository.getProjectDetails(randomProjectId()));
    }

    @Test
    void updateProject_shouldReturnUpdate_whenAllConditionsMet() {
        final var newOwner = saveAndGetTestUser();
        final var projectToUpdate = saveAndGetTestProject();
        projectToUpdate.setTitle("New title");
        projectToUpdate.setDescription("New description");
        projectToUpdate.setOwner(newOwner);
        final var updated = projectRepository.update(projectToUpdate);
        assertEquals(projectToUpdate, updated);
    }

    @Test
    void updateProject_shouldThrowEntityNotFoundException_whenProjectDoesNotExist() {
        final var projectToUpdate = Project.builder()
                .id(randomProjectId())
                .title("title")
                .description("description")
                .owner(saveAndGetTestUser())
                .build();
        assertThrows(
                EntityNotFoundException.class,
                () -> projectRepository.update(projectToUpdate)
        );
    }

    @Test
    void hasMember_shouldReturnTrue_whenUserIsMember() {
        final var project = saveAndGetTestProject();
        assertTrue(projectRepository.hasMember(project.getId(), project.getOwner().id()));
    }

    @Test
    void hasMember_shouldReturnFalse_whenUserIsNotMember() {
        final var project = saveAndGetTestProject();
        assertFalse(projectRepository.hasMember(project.getId(), new UserId(randomLong())));
    }

    @Test
    void addMember_shouldAddProjectMember() {
        final var givenProjectId = saveAndGetTestProject().getId();
        final var givenMemberId = saveAndGetTestUser().id();
        projectRepository.addMember(givenProjectId, givenMemberId);
        final var projectEntity = jpaProjectRepository.findById(givenProjectId.value()).orElseThrow();
        assertTrue(projectEntity.getMembers().stream().anyMatch(userEntity -> givenMemberId.value().equals(userEntity.getId())));
    }

    @Test
    void addMember_shouldThrowEntityNotFoundException_whenProjectEntityDoesNotExist() {
        final var givenProjectId = randomProjectId();
        final var givenMemberId = saveAndGetTestUser().id();
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

    private void assertMatches(ProjectEntity expected, ProjectDetailsDTO actual) {
        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertMatches(expected.getOwner(), actual.owner());
        for (int i = 0; i < expected.getMembers().size(); i++) {
            assertMatches(expected.getMembers().get(i), actual.members().get(i));
        }
    }

    private void assertMatches(UserEntity expected, ProjectUserDTO actual) {
        assertEquals(expected.getId(), actual.id());
        assertEquals(expected.getEmail(), actual.email());
        assertEquals(expected.getFirstName(), actual.firstName());
        assertEquals(expected.getLastName(), actual.lastName());
    }

    private long countProjectEntitiesByMemberId(Long memberId) {
        return jpaProjectRepository.findAll().stream()
                .filter(containsMemberWithIdPredicate(memberId))
                .count();
    }

    private ProjectUser saveAndGetTestUser() {
        final var user = userRepository.add(getTestUser());
        return ProjectUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private Project saveAndGetTestProject() {
        return saveAndGetTestProject(saveAndGetTestUser());
    }

    private Project saveAndGetTestProject(final ProjectUser owner) {
        return projectRepository.add(getTestProject(owner.id()));
    }

    private static Predicate<ProjectEntity> containsMemberWithIdPredicate(Long memberId) {
        return projectEntity -> projectEntity.getMembers().stream().anyMatch(userEntity -> memberId.equals(userEntity.getId()));
    }

    private static Project getTestProject() {
        return getTestProject(new UserId(randomLong()));
    }

    private static Project getTestProject(UserId ownerId) {
        final var randomLong = randomLong();
        final var owner = ProjectUser.withId(ownerId);
        return Project.builder()
                .title("Project %d".formatted(randomLong))
                .description("Project %d description".formatted(randomLong))
                .owner(owner)
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