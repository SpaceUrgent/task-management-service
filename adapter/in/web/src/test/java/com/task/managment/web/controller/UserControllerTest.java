package com.task.managment.web.controller;

import com.task.management.domain.iam.model.UserId;
import com.task.management.domain.iam.model.UserProfile;
import com.task.management.domain.iam.port.in.GetUserProfileUseCase;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest(controllerClass = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GetUserProfileUseCase getUserProfileUseCase;

    @MockUser
    @Test
    void getUserProfile_shouldReturnUser_whenAllConditionsMet() throws Exception {
        final var expectedUser = getUserProfile();
        doReturn(expectedUser).when(getUserProfileUseCase).getUserProfile(eq(TestUtils.DEFAULT_USER_ID));
        final var apiActionResult = mockMvc.perform(get("/api/users/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        assertMatches(expectedUser, apiActionResult);
    }

    @WithAnonymousUser
    @Test
    void getUserProfile_shouldReturnUnauthorized_whenUserNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/users/profile"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private void assertMatches(UserProfile expectedUser, ResultActions apiActionResult) throws Exception {
        apiActionResult
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedUser.id().value()))
                .andExpect(jsonPath("$.email").value(expectedUser.email()))
                .andExpect(jsonPath("$.firstName").value(expectedUser.firstName()))
                .andExpect(jsonPath("$.lastName").value(expectedUser.lastName()));
    }

    private static UserProfile getUserProfile() {
        return UserProfile.builder()
                .id(new UserId(MockUser.DEFAULT_USER_ID_VALUE))
                .email("user-%d@mail.com".formatted(MockUser.DEFAULT_USER_ID_VALUE))
                .firstName("FName-%d".formatted(MockUser.DEFAULT_USER_ID_VALUE))
                .lastName("LName-%d".formatted(MockUser.DEFAULT_USER_ID_VALUE))
                .build();
    }
}