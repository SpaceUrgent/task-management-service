package com.task.managment.web.iam;

import com.task.management.application.shared.UseCaseException;
import com.task.management.application.iam.CurrentPasswordMismatchException;
import com.task.management.application.iam.EmailExistsException;
import com.task.management.application.iam.command.RegisterUserCommand;
import com.task.management.application.iam.command.UpdateNameCommand;
import com.task.management.application.iam.command.UpdatePasswordCommand;
import com.task.management.application.iam.port.in.UserProfileUseCase;
import com.task.management.application.iam.port.in.RegisterUserUseCase;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.managment.web.shared.dto.UserInfoDto;
import com.task.managment.web.shared.mapper.UserInfoMapper;
import com.task.managment.web.iam.dto.request.UpdatePasswordRequest;
import com.task.managment.web.iam.dto.request.RegisterUserRequest;
import com.task.managment.web.shared.dto.ErrorResponse;
import com.task.managment.web.shared.BaseController;
import com.task.managment.web.iam.dto.request.UpdateUserProfileRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController extends BaseController {
    private final RegisterUserUseCase registerUserUseCase;
    private final UserProfileUseCase userProfileUseCase;
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
        final var userProfile = userProfileUseCase.getUserProfile(actor());
        return userProfileResponseMapper.toDto(userProfile);
    }

    @PutMapping
    public void updateUserProfile(@RequestBody @Valid @NotNull UpdateUserProfileRequest request) throws UseCaseException {
        final var command = UpdateNameCommand.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        userProfileUseCase.updateName(actor(), command);
    }

    @PostMapping(
            value = "/password",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public void updatePassword(@Valid @NotNull UpdatePasswordRequest request) throws UseCaseException {
        final var command = UpdatePasswordCommand.builder()
                .currentPassword(request.getCurrentPassword())
                .newPassword(request.getNewPassword())
                .build();
        userProfileUseCase.updatePassword(actor(), command);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(EmailExistsException.class)
//    public ErrorResponse handleEmailExistsException(EmailExistsException exception, HttpServletRequest request) {
//        return ErrorResponse.builder()
//                .reason("Bad request")
//                .message(exception.getMessage())
//                .request(request)
//                .build();
//    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EmailExistsException.class, CurrentPasswordMismatchException.class })
    public ErrorResponse handleEmailExistsException(UseCaseException exception, HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason("Bad request")
                .message(exception.getMessage())
                .request(request)
                .build();
    }
}
