package com.task.managment.web.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.managment.web.WebProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(WebProperties.class)
public class SessionBasedSecurityConfiguration {

    private final WebProperties webProperties;
    private final AuthenticationProvider authenticationProvider;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exceptionHandlingConfigurer -> {
                    exceptionHandlingConfigurer.authenticationEntryPoint(new ResponseBodyAuthEntryPoint(objectMapper));
                })
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/api/users/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .securityContext(context -> context.securityContextRepository(securityContextRepository()))
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

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    public CorsConfigurationSource corsConfigurationSource() {
        final var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(webProperties.getAllowedOrigins());
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Content-Type"));
        corsConfiguration.setAllowCredentials(true);
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
