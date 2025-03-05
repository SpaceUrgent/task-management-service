package com.task.managment.web.controller;


import com.task.management.domain.iam.exception.EmailExistsException;
import com.task.management.domain.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.iam.port.in.command.RegisterUserCommand;
import com.task.managment.web.TestUtils;
import com.task.managment.web.WebTest;
import com.task.managment.web.dto.request.RegisterUserRequest;
import com.task.managment.web.security.SecuredUser;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Arrays;
import java.util.Objects;

import static com.task.managment.web.TestUtils.EMAIL;
import static com.task.managment.web.TestUtils.FIRST_NAME;
import static com.task.managment.web.TestUtils.LAST_NAME;
import static com.task.managment.web.TestUtils.PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest(controllerClass = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegisterUserUseCase registerUserUseCase;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @WithAnonymousUser
    @Test
    void login_shouldReturnOk_whenAllConditionsMet() throws Exception {
        final var userCredentials = TestUtils.DEFAULT_CREDENTIALS;
        doReturn(true).when(passwordEncoder).matches(eq(PASSWORD), eq(userCredentials.encryptedPassword()));
        doReturn(new SecuredUser(userCredentials)).when(userDetailsService).loadUserByUsername(eq(userCredentials.email()));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", EMAIL)
                        .param("password", PASSWORD))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void login_shouldReturnUnauthorized_whenPasswordDoesNotMatch() throws Exception {
        final var userCredentials = TestUtils.DEFAULT_CREDENTIALS;
        doReturn(false).when(passwordEncoder).matches(eq(PASSWORD), eq(userCredentials.encryptedPassword()));
        doReturn(new SecuredUser(userCredentials)).when(userDetailsService).loadUserByUsername(eq(userCredentials.email()));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", EMAIL)
                        .param("password", PASSWORD))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @WithAnonymousUser
    @Test
    void login_shouldReturnUnauthorized_whenUserNotFound() throws Exception {
        doThrow(new UsernameNotFoundException("User not found")).when(userDetailsService).loadUserByUsername(any());
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", EMAIL)
                        .param("password", PASSWORD))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_shouldReturnOk_whenAllConditionsMet() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }

    @WithAnonymousUser
    @Test
    void register_shouldReturnRegisteredUserDto_whenAllConditionsMet() throws Exception {
        final var givenRequest = getRegisterUserRequest();
        final var expectedCommand = toCommand(givenRequest);
        mockMvc.perform(post("/api/auth/register")
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
        final var errorMessage = "User with email '%s' exits".formatted(givenRequest.getEmail());
        doThrow(new EmailExistsException(errorMessage))
                .when(registerUserUseCase)
                .register(argThat(registerUserCommandArgumentMatcher(givenRequest)));
        mockMvc.perform(post("/api/auth/register")
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
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/auth/register"));
    }

    private static RegisterUserRequest getRegisterUserRequest() {
        final var registerUserDto = new RegisterUserRequest();
        registerUserDto.setEmail(EMAIL);
        registerUserDto.setFirstName(FIRST_NAME);
        registerUserDto.setLastName(LAST_NAME);
        registerUserDto.setPassword(PASSWORD.toCharArray());
        return registerUserDto;
    }

    private static RegisterUserCommand toCommand(RegisterUserRequest givenRequest) {
        return RegisterUserCommand.builder()
                .email(givenRequest.getEmail())
                .firstName(givenRequest.getFirstName())
                .lastName(givenRequest.getLastName())
                .password(givenRequest.getPassword())
                .build();
    }

    private static ArgumentMatcher<RegisterUserCommand> registerUserCommandArgumentMatcher(RegisterUserRequest request) {
        return command -> Objects.equals(request.getEmail(), command.email())
                && Objects.equals(request.getFirstName(), command.firstName())
                && Objects.equals(request.getLastName(), command.lastName())
                && Arrays.equals(request.getPassword(), command.password());
    }
}