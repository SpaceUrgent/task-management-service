package com.task.managment.web.iam;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.application.UseCaseException;
import com.task.management.domain.iam.application.EmailExistsException;
import com.task.management.domain.common.model.UserId;
import com.task.management.domain.iam.port.in.GetUserProfileUseCase;
import com.task.management.domain.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.iam.application.command.RegisterUserCommand;
import com.task.managment.web.iam.dto.UserProfileDto;
import com.task.managment.web.iam.dto.request.RegisterUserRequest;
import com.task.managment.web.ErrorResponse;
import com.task.managment.web.iam.mapper.UserMapper;
import com.task.managment.web.security.SecuredUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UserMapper userProfileResponseMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public void register(@Valid RegisterUserRequest request) throws EmailExistsException {
        final var command = RegisterUserCommand.builder()
                .email(new Email(request.getEmail()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(request.getPassword())
                .build();
        registerUserUseCase.register(command);
    }

    @GetMapping(
            value = "/profile",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public UserProfileDto getUserProfile() throws UseCaseException {
        final var userProfile = getUserProfileUseCase.getUserProfile(actorId());
        return userProfileResponseMapper.toDto(userProfile);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmailExistsException.class)
    public ErrorResponse handleEmailExistsException(EmailExistsException exception, HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason("Bad request")
                .message(exception.getMessage())
                .request(request)
                .build();
    }

    private UserId actorId() {
        return new UserId(actor().getId());
    }

    private SecuredUser actor() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecuredUser) authentication.getPrincipal());
    }
}
