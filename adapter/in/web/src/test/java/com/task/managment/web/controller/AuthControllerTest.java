package com.task.managment.web.controller;

import com.task.management.application.dto.UserDTO;
import com.task.management.application.exception.EmailExistsException;
import com.task.management.application.port.in.RegisterUserUseCase;
import com.task.management.application.dto.RegisterUserDto;
import com.task.management.application.port.out.FindUserPort;
import com.task.managment.web.TestUtils;
import com.task.managment.web.WebTest;
import com.task.managment.web.WebTestConfiguration;
import com.task.managment.web.security.SecuredUser;
import com.task.managment.web.security.SecurityConfiguration;
import com.task.managment.web.security.UserDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import static com.task.managment.web.TestUtils.EMAIL;
import static com.task.managment.web.TestUtils.FIRST_NAME;
import static com.task.managment.web.TestUtils.LAST_NAME;
import static com.task.managment.web.TestUtils.PASSWORD;
import static com.task.managment.web.TestUtils.randomLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
    private FindUserPort findUserPort;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @WithAnonymousUser
    @Test
    void login_shouldReturnOk_whenAllConditionsMet() throws Exception {
        final var user = TestUtils.DEFAULT_USER;
        doReturn(true).when(passwordEncoder).matches(eq(PASSWORD), eq(user.getEncryptedPassword()));
        doReturn(new SecuredUser(user)).when(userDetailsService).loadUserByUsername(eq(user.getEmail()));
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
        final var user = TestUtils.DEFAULT_USER;
        doReturn(false).when(passwordEncoder).matches(eq(PASSWORD), eq(user.getEncryptedPassword()));
        doReturn(new SecuredUser(user)).when(userDetailsService).loadUserByUsername(eq(user.getEmail()));
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
        final var givenRequestDto = getRegisterUserDto();
        doAnswer(registerUserAnswer()).when(registerUserUseCase).register(eq(givenRequestDto));
        mockMvc.perform(post("/api/auth/register")
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
        mockMvc.perform(post("/api/auth/register")
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
                .andExpect(jsonPath("$.path").value("/api/auth/register"));
    }


    private static RegisterUserDto getRegisterUserDto() {
        final var registerUserDto = new RegisterUserDto();
        registerUserDto.setEmail(EMAIL);
        registerUserDto.setFirstName(FIRST_NAME);
        registerUserDto.setLastName(LAST_NAME);
        registerUserDto.setPassword(PASSWORD.toCharArray());
        return registerUserDto;
    }

    private static Answer<UserDTO> registerUserAnswer() {
        return invocation -> {
            final var registerUserDto = (RegisterUserDto) invocation.getArgument(0);
            return UserDTO.builder()
                    .id(randomLong())
                    .email(registerUserDto.getEmail())
                    .firstName(registerUserDto.getFirstName())
                    .lastName(registerUserDto.getLastName())
                    .build();
        };
    }
}