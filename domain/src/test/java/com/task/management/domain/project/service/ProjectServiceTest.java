package com.task.management.domain.project.service;

import com.task.management.domain.common.application.service.UserInfoService;
import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.common.validation.ValidationService;
import com.task.management.domain.project.application.service.ProjectService;
import com.task.management.domain.project.model.*;
import com.task.management.domain.project.application.command.UpdateMemberRoleCommand;
import com.task.management.domain.project.application.command.UpdateProjectCommand;
import com.task.management.domain.project.application.command.CreateProjectCommand;
import com.task.management.domain.project.port.out.MemberRepositoryPort;
import com.task.management.domain.project.port.out.ProjectRepositoryPort;
import com.task.management.domain.project.projection.ProjectPreview;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.task.management.domain.project.ProjectTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @SuppressWarnings("unused")
    @Mock
    private ValidationService validationService;
    @Mock
    private UserInfoService userInfoService;
    @Mock
    private ProjectRepositoryPort projectRepositoryPort;
    @Mock
    private MemberRepositoryPort memberRepositoryPort;
    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldReturnNewProject_whenAllConditionsMet() {
        final var command = createProjectCommand();
        final var givenActorId = randomUserId();
        final var projectCaptor = ArgumentCaptor.forClass(Project.class);
        doAnswer(self(Project.class)).when(projectRepositoryPort).save(projectCaptor.capture());
        projectService.createProject(givenActorId, command);
        final var created = projectCaptor.getValue();
        assertNotNull(created.getCreatedAt());
        assertEquals(command.title(), created.getTitle());
        assertEquals(command.description(), created.getDescription());
    }

    @Test
    void getAvailableProjects_shouldReturnProjectList() {
        final var expectedProjects = randomProjectPreviews();
        final var givenActorId = randomUserId();
        doReturn(expectedProjects).when(projectRepositoryPort).findProjectsByMember(eq(givenActorId));
        assertEquals(expectedProjects, projectService.getAvailableProjects(givenActorId));
    }

    @Test
    void updateProject_shouldReturnUpdated_whenAllConditionsMet() throws UseCaseException {
        var project = randomProject();
        final var givenCommand = updateProjectCommand();
        final var givenActorId = project.getOwnerId();
        final var projectCaptor = ArgumentCaptor.forClass(Project.class);
        final var actor = Member.builder()
                .id(givenActorId)
                .projectId(project.getId())
                .role(MemberRole.OWNER)
                .build();
        doReturn(Optional.of(actor)).when(memberRepositoryPort).find(eq(project.getId()), eq(givenActorId));
        doReturn(Optional.of(project)).when(projectRepositoryPort).find(eq(project.getId()));
        doAnswer(self(Project.class)).when(projectRepositoryPort).save(projectCaptor.capture());
        projectService.updateProject(givenActorId, project.getId(), givenCommand);
        final var saved = projectCaptor.getValue();
        assertNotNull(saved.getUpdatedAt());
        assertEquals(givenCommand.title(), saved.getTitle());
        assertEquals(givenCommand.description(), saved.getDescription());
    }

    @Test
    void updateProject_shouldThrowIllegalAccessException_whenActorIsNotMember() {
        var project = randomProject();
        final var givenCommand = updateProjectCommand();
        final var givenActorId = randomUserId();
        doReturn(Optional.empty()).when(memberRepositoryPort).find(eq(project.getId()), eq(givenActorId));
        lenient().doReturn(Optional.of(project)).when(projectRepositoryPort).find(eq(project.getId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.updateProject(givenActorId, project.getId(), givenCommand)
        );
        verify(projectRepositoryPort, times(0)).save(any());
    }

    @Test
    void updateProject_shouldThrowIllegalAccessException_whenActorDoesNotAllowed() {
        var project = randomProject();
        final var givenCommand = updateProjectCommand();
        final var givenActorId = randomUserId();
        final var actor = Member.create(givenActorId, project.getId());
        doReturn(Optional.of(actor)).when(memberRepositoryPort).find(eq(project.getId()), eq(givenActorId));
        doReturn(Optional.of(project)).when(projectRepositoryPort).find(eq(project.getId()));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.updateProject(givenActorId, project.getId(), givenCommand)
        );
        verify(projectRepositoryPort, times(0)).save(any());
    }

    @Test
    void addMember_shouldAddMember_whenAllConditionsMet() throws Exception {
        final var memberInfo = randomUserInfo();
        final var givenActorId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenEmail = new Email("username@domain.com");
        final var actor = Member.create(givenActorId, givenProjectId);
        final var expectedMember = Member.create(memberInfo.id(), givenProjectId);
        doReturn(Optional.of(actor)).when(memberRepositoryPort).find(eq(givenProjectId), eq(givenActorId));
        doReturn(memberInfo).when(userInfoService).getUser(eq(givenEmail));
        projectService.addMember(givenActorId, givenProjectId, givenEmail);
        verify(memberRepositoryPort).save(eq(expectedMember));
    }

    @Test
    void addMember_shouldThrowIllegalAccessException_whenCurrentUserIsNotProjectMember() {
        final var givenActorId = randomUserId();
        final var givenProjectId = randomProjectId();
        final var givenEmail = new Email ("username@domain.com");
        doReturn(Optional.empty()).when(memberRepositoryPort).find(eq(givenProjectId), eq(givenActorId));
        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.addMember(givenActorId, givenProjectId, givenEmail)
        );
        verify(memberRepositoryPort, times(0)).save(any());
    }

    @Test
    void updateMemberRole_shouldUpdate_whenOwnerRoleIsPassed() throws UseCaseException {
        final var projectId = randomProjectId();

        final var actingMember = createMember(projectId, MemberRole.OWNER);
        final var updatedMember = createMember(projectId, null);
        final var givenCommand = UpdateMemberRoleCommand.builder()
                .projectId(projectId)
                .memberId(updatedMember.getId())
                .role(MemberRole.OWNER)
                .build();
        final var givenActorId = actingMember.getId();
        doReturn(Optional.of(actingMember)).when(memberRepositoryPort).find(eq(projectId), eq(givenActorId));
        doReturn(Optional.of(updatedMember)).when(memberRepositoryPort).find(eq(projectId), eq(updatedMember.getId()));

        projectService.updateMemberRole(givenActorId, givenCommand);

        assertEquals(MemberRole.ADMIN, actingMember.getRole());
        assertEquals(MemberRole.OWNER, updatedMember.getRole());
        verify(memberRepositoryPort).save(actingMember);
        verify(memberRepositoryPort).save(updatedMember);
    }

    @Test
    void updateMemberRole_shouldUpdate_whenOwnerPromotesToAdmin() throws UseCaseException {
        final var projectId = randomProjectId();
        final var actingMember = createMember(projectId, MemberRole.OWNER);
        final var updatedMember = createMember(projectId, null);
        final var givenCommand = UpdateMemberRoleCommand.builder()
                .projectId(projectId)
                .memberId(updatedMember.getId())
                .role(MemberRole.ADMIN)
                .build();
        final var givenActorId = actingMember.getId();
        doReturn(Optional.of(actingMember)).when(memberRepositoryPort).find(eq(projectId), eq(givenActorId));
        doReturn(Optional.of(updatedMember)).when(memberRepositoryPort).find(eq(projectId), eq(updatedMember.getId()));

        projectService.updateMemberRole(givenActorId, givenCommand);

        assertEquals(givenCommand.role(), updatedMember.getRole());
        verify(memberRepositoryPort).save(updatedMember);
    }

    @Test
    void updateMemberRole_shouldUpdate_whenOwnerRemovesRole() throws UseCaseException {
        final var projectId = randomProjectId();
        final var actingMember = createMember(projectId, MemberRole.OWNER);
        final var updatedMember = createMember(projectId, null);
        final var givenCommand = UpdateMemberRoleCommand.builder()
                .projectId(projectId)
                .memberId(updatedMember.getId())
                .build();
        final var givenActorId = actingMember.getId();
        doReturn(Optional.of(actingMember)).when(memberRepositoryPort).find(eq(projectId), eq(givenActorId));
        doReturn(Optional.of(updatedMember)).when(memberRepositoryPort).find(eq(projectId), eq(updatedMember.getId()));

        projectService.updateMemberRole(givenActorId, givenCommand);

        assertEquals(givenCommand.role(), updatedMember.getRole());
        verify(memberRepositoryPort).save(updatedMember);
    }

    @Test
    void updateMemberRole_throwsIllegalAccess_whenActorIsNotMember() {
        final var givenActorId = randomMemberId();
        final var givenCommand = UpdateMemberRoleCommand.builder()
                .projectId(randomProjectId())
                .memberId(randomMemberId())
                .role(MemberRole.ADMIN)
                .build();
        doReturn(Optional.empty()).when(memberRepositoryPort).find(eq(givenCommand.projectId()), eq(givenActorId));

        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.updateMemberRole(givenActorId, givenCommand)
        );
    }

    @Test
    void updateMemberRole_throwsEntityNotFound_whenUpdatedMemberNotFound() {
        final var projectId = randomProjectId();
        final var actingMember = createMember(projectId, null);
        final var givenCommand = UpdateMemberRoleCommand.builder()
                .projectId(projectId)
                .memberId(randomMemberId())
                .role(MemberRole.ADMIN)
                .build();
        doReturn(Optional.of(actingMember)).when(memberRepositoryPort).find(eq(projectId), eq(actingMember.getId()));
        doReturn(Optional.empty()).when(memberRepositoryPort).find(eq(projectId), eq(givenCommand.memberId()));

        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> projectService.updateMemberRole(actingMember.getId(), givenCommand)
        );
    }

    @Test
    void updateMemberRole_shouldThrowIllegalAccess_whenAdminUpdating() {
        final var projectId = randomProjectId();
        final var actingMember = createMember(projectId, MemberRole.ADMIN);
        final var updatedMember = createMember(projectId, null);
        final var givenCommand = UpdateMemberRoleCommand.builder()
                .projectId(projectId)
                .memberId(updatedMember.getId())
                .role(MemberRole.ADMIN)
                .build();
        final var givenActorId = actingMember.getId();
        doReturn(Optional.of(actingMember)).when(memberRepositoryPort).find(eq(projectId), eq(givenActorId));
        doReturn(Optional.of(updatedMember)).when(memberRepositoryPort).find(eq(projectId), eq(updatedMember.getId()));

        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.updateMemberRole(givenActorId, givenCommand)
        );

        verifyNoMoreInteractions(memberRepositoryPort);
    }

    @Test
    void updateMemberRole_shouldThrowIllegalAccess_whenMemberUpdating() {
        final var projectId = randomProjectId();
        final var actingMember = createMember(projectId, null);
        final var updatedMember = createMember(projectId, null);
        final var givenCommand = UpdateMemberRoleCommand.builder()
                .projectId(projectId)
                .memberId(updatedMember.getId())
                .role(MemberRole.ADMIN)
                .build();
        final var givenActorId = actingMember.getId();
        doReturn(Optional.of(actingMember)).when(memberRepositoryPort).find(eq(projectId), eq(givenActorId));
        doReturn(Optional.of(updatedMember)).when(memberRepositoryPort).find(eq(projectId), eq(updatedMember.getId()));

        assertThrows(
                UseCaseException.IllegalAccessException.class,
                () -> projectService.updateMemberRole(givenActorId, givenCommand)
        );

        verifyNoMoreInteractions(memberRepositoryPort);
    }

    private List<ProjectPreview> randomProjectPreviews() {
        return IntStream.range(0, 10)
                .mapToObj(value -> rendomProjectPreview())
                .toList();
    }

    private static ProjectPreview rendomProjectPreview() {
        final var projectId = randomProjectId();
        final var projectIdValue = projectId.value();
        return ProjectPreview.builder()
                .id(projectId)
                .title("Title %d".formatted(projectIdValue))
                .title("Description %d".formatted(projectIdValue))
                .owner(randomMemberView())
                .build();
    }

    private static Project randomProject() {
        final var projectId = randomProjectId();
        return Project.builder()
                .id(projectId)
                .createdAt(Instant.now())
                .title("Title %d".formatted(projectId.value()))
                .description("Description %d".formatted(projectId.value()))
                .ownerId(randomUserId())
                .build();
    }

    private CreateProjectCommand createProjectCommand() {
        return CreateProjectCommand.builder()
                .title("New project title")
                .description("New project description")
                .build();
    }

    private static UpdateProjectCommand updateProjectCommand() {
        return UpdateProjectCommand.builder()
                .title("Update title")
                .description("Updated description")
                .build();
    }

    private Member createMember(ProjectId projectId, MemberRole memberRole) {
        final var idValue = randomLong();
        return Member.builder()
                .id(new UserId(idValue))
                .projectId(projectId)
                .role(memberRole)
                .build();
    }

    private UserId randomMemberId() {
        return new UserId(randomLong());
    }
}