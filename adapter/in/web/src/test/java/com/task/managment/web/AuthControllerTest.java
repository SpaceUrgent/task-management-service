package com.task.managment.web;


import com.task.managment.web.iam.AuthController;
import com.task.managment.web.security.MockUser;
import com.task.managment.web.security.SecuredUser;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.task.managment.web.TestUtils.EMAIL;
import static com.task.managment.web.TestUtils.PASSWORD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebTest(testClasses = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @WithAnonymousUser
    @Test
    void login_shouldReturnOk_whenAllConditionsMet() throws Exception {
        final var userCredentials = TestUtils.DEFAULT_CREDENTIALS;
        doReturn(true).when(passwordEncoder).matches(eq(PASSWORD), eq(userCredentials.encryptedPassword()));
        doReturn(new SecuredUser(userCredentials)).when(userDetailsService).loadUserByUsername(eq(userCredentials.email().value()));
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
        doReturn(new SecuredUser(userCredentials)).when(userDetailsService).loadUserByUsername(eq(userCredentials.email().value()));
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

    @MockUser
    @Test
    void logout_shouldReturnOk_whenAllConditionsMet() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/api/auth/logout")
                        .session(session)
                        .cookie(new Cookie("JSESSIONID", session.getId())))
                .andExpect(status().isOk());
    }
}