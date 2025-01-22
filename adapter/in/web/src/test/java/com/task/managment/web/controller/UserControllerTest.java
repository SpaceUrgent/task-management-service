package com.task.managment.web.controller;

import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.model.User;
import com.task.management.application.model.UserId;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.port.in.dto.RegisterUserDto;
import com.task.management.application.port.out.UserRepository;
import com.task.managment.web.security.SecurityConfiguration;
import com.task.managment.web.security.UserDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Random;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {
                WebTestConfiguration.class,
                GlobalExceptionHandler.class,
                UserController.class,
                SecurityConfiguration.class,
                UserDetailServiceImpl.class
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegisterUserUseCase registerUserUseCase;
    @MockBean
    private UserRepository userRepository;

    @WithAnonymousUser
    @Test
    void register_shouldReturnRegisteredUserDto_whenAllConditionsMet() throws Exception {
        final var givenRequestDto = getRegisterUserDto();
        doAnswer(registerUserAnswer()).when(registerUserUseCase).register(eq(givenRequestDto));
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(new LinkedMultiValueMap<>() {{
                            add("email", givenRequestDto.getEmail());
                            add("firstName", givenRequestDto.getFirstName());
                            add("lastName", givenRequestDto.getLastName());
                            add("password", new String(givenRequestDto.getPassword()));
                        }}))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(givenRequestDto.getEmail()))
                .andExpect(jsonPath("$.firstName").value(givenRequestDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(givenRequestDto.getLastName()));
    }

    @WithAnonymousUser
    @Test
    void register_shouldReturnBadRequest_whenUserWithGivenEmailExists() throws Exception {
        final var givenRequestDto = getRegisterUserDto();
        final var errorMessage = "User with email '%s' exits".formatted(givenRequestDto.getEmail());
        doThrow(new EmailExistsException(errorMessage)).when(registerUserUseCase).register(eq(givenRequestDto));
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(new LinkedMultiValueMap<>() {{
                            add("email", givenRequestDto.getEmail());
                            add("firstName", givenRequestDto.getFirstName());
                            add("lastName", givenRequestDto.getLastName());
                            add("password", new String(givenRequestDto.getPassword()));
                        }}))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.path").value("/api/users/register"));
    }

    @WithAnonymousUser
    @Test
    void register_shouldReturnBadRequest_whenNoParametersPresent() throws Exception {
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value("Bad request"))
                .andExpect(jsonPath("$.message").value("Request validation error"))
                .andExpect(jsonPath("$.errors.firstName").value("First name is required"))
                .andExpect(jsonPath("$.errors.lastName").value("Last name is required"))
                .andExpect(jsonPath("$.errors.email").value("Email is required"))
                .andExpect(jsonPath("$.errors.password").value("Password is required"))
                .andExpect(jsonPath("$.path").value("/api/users/register"));
    }

    private static RegisterUserDto getRegisterUserDto() {
        final var registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail("test@domain.com");
        registerUserDto.setFirstName("John");
        registerUserDto.setLastName("Doe");
        registerUserDto.setPassword("password123".toCharArray());
        return registerUserDto;
    }

    private static Answer<User> registerUserAnswer() {
        return invocation -> {
            final var registerUserDto = (RegisterUserDto) invocation.getArgument(0);
            return User.builder()
                    .id(new UserId(new Random().nextLong()))
                    .email(registerUserDto.getEmail())
                    .firstName(registerUserDto.getFirstName())
                    .lastName(registerUserDto.getLastName())
                    .encryptedPassword("encryptedPassword")
                    .build();
        };
    }
}