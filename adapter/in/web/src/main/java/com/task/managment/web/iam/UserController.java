package com.task.managment.web.iam;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.application.iam.port.in.GetUserProfileUseCase;
import com.task.management.application.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.common.model.Email;
import com.task.managment.web.common.dto.UserInfoDto;
import com.task.managment.web.common.mapper.UserInfoMapper;
import com.task.managment.web.iam.dto.request.RegisterUserRequest;
import com.task.managment.web.common.dto.ErrorResponse;
import com.task.managment.web.common.BaseController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final RegisterUserUseCase registerUserUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final UserInfoMapper userProfileResponseMapper;

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
    public UserInfoDto getUserProfile() throws UseCaseException {
        final var userProfile = getUserProfileUseCase.getUserProfile(actor());
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
}
