package com.task.managment.web;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "web")
public class WebProperties {
    @NotEmpty(message = "Allowed origins list is required")
    private List<String> allowedOrigins;
}
