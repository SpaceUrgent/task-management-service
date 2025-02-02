package com.task.managment.web.controller;

import com.task.management.application.dto.UserDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.port.in.GetUserByEmailUseCase;
import com.task.management.application.port.in.GetUserUseCase;
import com.task.managment.web.TestUtils;
import com.task.managment.web.WebTest;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GetUserUseCase getUserUseCase;
    @MockBean
    private GetUserByEmailUseCase getUserByEmailUseCase;

    @MockUser
    @Test
    void getUserByEmail_shouldReturnUser_whenAllConditionsMet() throws Exception {
        final var expectedUser = getUserDTO(MockUser.DEFAULT_USER_ID_VALUE);
        final var givenEmail = expectedUser.email();
        doReturn(expectedUser).when(getUserByEmailUseCase).getUser(eq(givenEmail));
        final var apiActionResult = mockMvc.perform(get("/api/users/email/{email}", givenEmail))
                .andDo(print())
                .andExpect(status().isOk());
        assertMatches(expectedUser, apiActionResult);
    }

    @MockUser
    @Test
    void getUserByEmail_shouldReturnInternalError_whenUserNotFound() throws Exception {
        final var errorMessage = "User not found";
        final var givenEmail = "user@mail.com";
        doThrow(new EntityNotFoundException(errorMessage)).when(getUserByEmailUseCase).getUser(eq(givenEmail));
        mockMvc.perform(get("/api/users/email/{email}", givenEmail))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Entity not found"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/users/email/%s".formatted(givenEmail)));
    }

    @MockUser
    @Test
    void getUserProfile_shouldReturnUser_whenAllConditionsMet() throws Exception {
        final var expectedUser = getUserDTO(MockUser.DEFAULT_USER_ID_VALUE);
        doReturn(expectedUser).when(getUserUseCase).getUser(eq(TestUtils.DEFAULT_USER_ID));
        final var apiActionResult = mockMvc.perform(get("/api/users/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertMatches(expectedUser, apiActionResult);
    }

    @MockUser
    @Test
    void getUserProfile_shouldReturnInternalError_whenAllUserNotFound() throws Exception {
        final var errorMessage = "User not found";
        doThrow(new EntityNotFoundException(errorMessage)).when(getUserUseCase).getUser(any());
        mockMvc.perform(get("/api/users/profile"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Entity not found"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/users/profile"));
    }

    @WithAnonymousUser
    @Test
    void getUserProfile_shouldReturnUnauthorized_whenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private void assertMatches(UserDTO expectedUser, ResultActions apiActionResult) throws Exception {
        apiActionResult.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedUser.id()))
                .andExpect(jsonPath("$.email").value(expectedUser.email()))
                .andExpect(jsonPath("$.firstName").value(expectedUser.firstName()))
                .andExpect(jsonPath("$.lastName").value(expectedUser.lastName()));
    }

    private static UserDTO getUserDTO(Long userIdValue) {
        return UserDTO.builder()
                .id(userIdValue)
                .email("user-%d@mail.com".formatted(userIdValue))
                .firstName("FName-%d".formatted(userIdValue))
                .lastName("LName-%d".formatted(userIdValue))
                .build();
    }
}