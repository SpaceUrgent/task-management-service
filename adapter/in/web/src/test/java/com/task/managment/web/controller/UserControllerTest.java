package com.task.managment.web.controller;

import com.task.management.application.exception.UserNotFoundException;
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

    @MockUser
    @Test
    void getUserProfile_shouldReturnUser_whenAllConditionsMet() throws Exception {
        final var expectedUser = TestUtils.DEFAULT_USER;
        doReturn(expectedUser).when(getUserUseCase).getUser(eq(TestUtils.DEFAULT_USER_ID));
        mockMvc.perform(get("/api/users/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedUser.getId().value()))
                .andExpect(jsonPath("$.email").value(expectedUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(expectedUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(expectedUser.getLastName()));
    }

    @MockUser
    @Test
    void getUserProfile_shouldReturnBadRequest_whenAllUserNotFound() throws Exception {
        final var errorMessage = "User not found";
        doThrow(new UserNotFoundException(errorMessage)).when(getUserUseCase).getUser(any());
        mockMvc.perform(get("/api/users/profile"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
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
}