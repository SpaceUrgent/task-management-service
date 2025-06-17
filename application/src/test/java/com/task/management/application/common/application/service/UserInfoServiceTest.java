package com.task.management.application.common.application.service;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.common.port.out.UserInfoRepositoryPort;
import com.task.management.application.common.service.UserInfoService;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.application.project.ProjectTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {
    @Mock
    private UserInfoRepositoryPort projectUserRepositoryPort;
    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    void getProjectUserWithEmail_shouldReturnProjectUser_whenAllConditionsMet() throws UseCaseException.EntityNotFoundException {
        final var expectedUser = ProjectTestUtils.randomUserInfo();
        final var givenEmail = expectedUser.email();
        doReturn(Optional.of(expectedUser)).when(projectUserRepositoryPort).find(eq(givenEmail));
        assertEquals(expectedUser, userInfoService.getUser(givenEmail));
    }

    @Test
    void getProjectUserWithEmail_shouldThrowEntityNotFoundException_whenProjectUserDoesNotExist() {
        final var givenEmail = new Email("random@email.com");
        doReturn(Optional.empty()).when(projectUserRepositoryPort).find(eq(givenEmail));
        assertThrows(
                UseCaseException.EntityNotFoundException.class,
                () -> userInfoService.getUser(givenEmail)
        );
    }
}