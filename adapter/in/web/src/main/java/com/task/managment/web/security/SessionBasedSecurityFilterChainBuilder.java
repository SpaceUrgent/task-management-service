package com.task.managment.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

import static java.util.Objects.requireNonNull;

public class SessionBasedSecurityFilterChainBuilder {

    private CorsConfigurationSource corsConfigurationSource;
    private AuthenticationProvider authenticationProvider;
    private ObjectMapper objectMapper;
    private SecurityContextRepository securityContextRepository;

    public SessionBasedSecurityFilterChainBuilder() {
    }

    public SessionBasedSecurityFilterChainBuilder corsConfigurationSource(@NotNull CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
        return this;
    }

    public SessionBasedSecurityFilterChainBuilder authenticationProvider(@NotNull AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    public SessionBasedSecurityFilterChainBuilder objectMapper(@NotNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        return this;
    }

    public SessionBasedSecurityFilterChainBuilder securityContextRepository(@NotNull SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
        return this;
    }

    public SecurityFilterChain build(HttpSecurity http) throws Exception {
        requireNonNull(http, "Http security is required");
        validate();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource))
                .exceptionHandling(exceptionHandlingConfigurer -> {
                    exceptionHandlingConfigurer.authenticationEntryPoint(new ResponseBodyAuthEntryPoint(objectMapper));
                })
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .securityContext(context -> context.securityContextRepository(securityContextRepository))
                .sessionManagement(session -> {
                    session.maximumSessions(3).maxSessionsPreventsLogin(true);
                    session.sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession);
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                })
                .logout((logout) -> {
                    logout.logoutUrl("/api/auth/logout");
                    logout.addLogoutHandler(
                            new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.COOKIES))
                    );
                    logout.deleteCookies("JSESSIONID");
                    logout.logoutSuccessHandler(((request, response, authentication) -> {
                        response.setStatus(200);
                    }));
                })
                .authenticationProvider(authenticationProvider);
        return http.build();
    }

    private void validate() {
        requireNonNull(corsConfigurationSource, "Cors configuration source is required");
        requireNonNull(authenticationProvider, "Authentication provider is required");
        requireNonNull(objectMapper, "Object mapper is required");
        requireNonNull(securityContextRepository, "Security context repository is required");
    }
}
