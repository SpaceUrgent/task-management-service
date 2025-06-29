package com.task.managment.web.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.iam.CurrentPasswordMismatchException;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.application.iam.command.UpdateNameCommand;
import com.task.management.application.iam.port.in.UserProfileUseCase;
import com.task.management.application.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.shared.model.UserInfo;
import com.task.managment.web.TestUtils;
import com.task.managment.web.WebTest;
import com.task.managment.web.iam.dto.request.RegisterUserRequest;
import com.task.managment.web.iam.dto.request.UpdateUserProfileRequest;
import com.task.managment.web.security.MockUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Arrays;
import java.util.Objects;

import static com.task.managment.web.TestUtils.EMAIL;
import static com.task.managment.web.TestUtils.FIRST_NAME;
import static com.task.managment.web.TestUtils.LAST_NAME;
import static com.task.managment.web.TestUtils.PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = {
        "com.task.managment.web.shared.mapper",
        "com.task.managment.web.iam.mapper"
})
@WebTest(testClasses = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserProfileUseCase userProfileUseCase;
    @MockBean
    private RegisterUserUseCase registerUserUseCase;
    @Autowired
    private ObjectMapper objectMapper;

    @WithAnonymousUser
    @Test
    void register_shouldReturnRegisteredUserDto_whenAllConditionsMet() throws Exception {
        final var givenRequest = getRegisterUserRequest();
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(new LinkedMultiValueMap<>() {{
                            add("email", givenRequest.getEmail());
                            add("firstName", givenRequest.getFirstName());
                            add("lastName", givenRequest.getLastName());
                            add("password", new String(givenRequest.getPassword()));
                        }}))
                .andExpect(status().isCreated());
        verify(registerUserUseCase).register(argThat(registerUserCommandArgumentMatcher(givenRequest)));
    }

    @WithAnonymousUser
    @Test
    void register_shouldReturnBadRequest_whenUserWithGivenEmailExists() throws Exception {
        final var givenRequest = getRegisterUserRequest();
        doThrow(new EmailExistsException(new Email(givenRequest.getEmail())))
                .when(registerUserUseCase)
                .register(argThat(registerUserCommandArgumentMatcher(givenRequest)));
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(new LinkedMultiValueMap<>() {{
                            add("email", givenRequest.getEmail());
                            add("firstName", givenRequest.getFirstName());
                            add("lastName", givenRequest.getLastName());
                            add("password", new String(givenRequest.getPassword()));
                        }}))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/users/register"));
    }

    @MockUser
    @Test
    void getUserProfile_shouldReturnUser_whenAllConditionsMet() throws Exception {
        final var expectedUser = getUserProfile();
        doReturn(expectedUser).when(userProfileUseCase).getUserProfile(eq(TestUtils.DEFAULT_USER_ID));
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

    @MockUser
    @Test
    void updateUserProfile_shouldReturnOk() throws Exception {
        final var request = updateUserProfileRequest();
        final var expectedCommand = UpdateNameCommand.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        mockMvc.perform(put("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk());
        verify(userProfileUseCase).updateName(eq(TestUtils.DEFAULT_USER_ID), eq(expectedCommand));
    }

    @MockUser
    @Test
    void updatePassword_shouldReturnOk_whenAllConditionsMet() throws Exception {
        final var givenNewPassword = "New password";
        final var givenOldPassword = "Old password";
        mockMvc.perform(post("/api/users/password")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .params(new LinkedMultiValueMap<>() {{
                        add("currentPassword", givenOldPassword);
                        add("newPassword", givenNewPassword);
                    }}))
                .andExpect(status().isOk());
        verify(userProfileUseCase).updatePassword(eq(TestUtils.DEFAULT_USER_ID), argThat(command -> {
            assertEquals(givenOldPassword, new String(command.currentPassword()));
            assertEquals(givenNewPassword, new String(command.newPassword()));
            return true;
        }));
    }

    @MockUser
    @Test
    void updatePassword_shouldReturnBadRequest_whenCurrentPasswordMismatchExceptionRaised() throws Exception {
        final var expectedMessage = "Current password does not match";
        doThrow(new CurrentPasswordMismatchException(expectedMessage))
                .when(userProfileUseCase)
                .updatePassword(any(), any());
        mockMvc.perform(post("/api/users/password")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(new LinkedMultiValueMap<>() {{
                            add("currentPassword", "Old password");
                            add("newPassword", "Old password");
                        }}))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/users/password"));
    }

    private static RegisterUserRequest getRegisterUserRequest() {
        final var registerUserDto = new RegisterUserRequest();
        registerUserDto.setEmail(EMAIL);
        registerUserDto.setFirstName(FIRST_NAME);
        registerUserDto.setLastName(LAST_NAME);
        registerUserDto.setPassword(PASSWORD.toCharArray());
        return registerUserDto;
    }

    private static UpdateUserProfileRequest updateUserProfileRequest() {
        final var request = new UpdateUserProfileRequest();
        request.setFirstName("New name");
        request.setLastName("New last name");
        return request;
    }

    private static UserInfo getUserProfile() {
        return UserInfo.builder()
                .id(new UserId(MockUser.DEFAULT_USER_ID_VALUE))
                .email(new Email("user-%d@mail.com".formatted(MockUser.DEFAULT_USER_ID_VALUE)))
                .firstName("FName-%d".formatted(MockUser.DEFAULT_USER_ID_VALUE))
                .lastName("LName-%d".formatted(MockUser.DEFAULT_USER_ID_VALUE))
                .build();
    }

    private static void assertMatches(UserInfo expectedUser, ResultActions apiActionResult) throws Exception {
        apiActionResult
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(expectedUser.id().value()))
                .andExpect(jsonPath("$.email").value(expectedUser.email().value()))
                .andExpect(jsonPath("$.firstName").value(expectedUser.firstName()))
                .andExpect(jsonPath("$.lastName").value(expectedUser.lastName()));
    }

    private static ArgumentMatcher<RegisterUserCommand> registerUserCommandArgumentMatcher(RegisterUserRequest request) {
        return command -> Objects.equals(request.getEmail(), command.email().value())
                && Objects.equals(request.getFirstName(), command.firstName())
                && Objects.equals(request.getLastName(), command.lastName())
                && Arrays.equals(request.getPassword(), command.password());
    }
}