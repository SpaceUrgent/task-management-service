package com.task.managment.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.managment.web.dto.ErrorDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnBadRequest_whenInvalidUrlEncodedRequest() throws Exception {
        final var uriPath = "/test/url-encoded-request";
        mockMvc.perform(post(uriPath)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(new LinkedMultiValueMap<>()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value(ErrorDTO.REASON_BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ErrorDTO.MESSAGE_INVALID_REQUEST))
                .andExpect(jsonPath("$.path").value(uriPath));
    }

    @Test
    void shouldReturnBadRequest_whenRequestBodyIsNotReceived() throws Exception {
        final var uriPath = "/test/json-request-body";
        mockMvc.perform(post(uriPath))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value(ErrorDTO.REASON_BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ErrorDTO.MESSAGE_MISSING_REQUEST_BODY))
                .andExpect(jsonPath("$.path").value(uriPath));
    }

    @Test
    void shouldReturnBadRequest_whenRequestBodyIsInvalid() throws Exception {
        final var uriPath = "/test/json-request-body";
        mockMvc.perform(post(uriPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TestRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value(ErrorDTO.REASON_BAD_REQUEST))
                .andExpect(jsonPath("$.message").value(ErrorDTO.MESSAGE_INVALID_REQUEST))
                .andExpect(jsonPath("$.errors.param").value("Param is required"))
                .andExpect(jsonPath("$.path").value(uriPath));
    }

    @Test
    void shouldReturnNotFound_whenEntityNotFoundExceptionThrown() throws Exception {
        final var uriPath = "/test/entity-not-found";
        mockMvc.perform(get(uriPath))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value(ErrorDTO.REASON_ENTITY_NOT_FOUND))
                .andExpect(jsonPath("$.message").value(TestController.ENTITY_NOT_FOUND_EXCEPTION.getMessage()))
                .andExpect(jsonPath("$.path").value(uriPath));
    }

    @Test
    void shouldReturnForbidden_whenInsufficientPrivilegesExceptionThrown() throws Exception {
        final var uriPath = "/test/insufficient-privileges";
        mockMvc.perform(get(uriPath))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.reason").value(ErrorDTO.REASON_ACTION_NOT_ALLOWED))
                .andExpect(jsonPath("$.message").value(TestController.INSUFFICIENT_PRIVILEGES_EXCEPTION.getMessage()))
                .andExpect(jsonPath("$.path").value(uriPath));
    }

    @RestController
    @RequestMapping("/test")
    static class TestController {
        static final EntityNotFoundException ENTITY_NOT_FOUND_EXCEPTION = new EntityNotFoundException("Test entity not found");
        static final InsufficientPrivilegesException INSUFFICIENT_PRIVILEGES_EXCEPTION = new InsufficientPrivilegesException("User not allowed to perform request");

        @PostMapping(
                path = "/url-encoded-request",
                consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
        )
        public void postWithUrlEncodedRequest(@Valid TestRequest request) {
        }

        @PostMapping("/json-request-body")
        public void postWithJsonRequestBody(@RequestBody @Valid @NotNull TestRequest testRequest) {
        }

        @GetMapping("/entity-not-found")
        public void throwEntityNotFoundException() throws EntityNotFoundException {
            throw ENTITY_NOT_FOUND_EXCEPTION;
        }

        @GetMapping("/insufficient-privileges")
        public void throwInsufficientPrivilegesException() throws InsufficientPrivilegesException {
            throw INSUFFICIENT_PRIVILEGES_EXCEPTION;
        }
    }

    @Data
    static class TestRequest {
        @NotNull(message = "Param is required")
        private String param;
    }
}