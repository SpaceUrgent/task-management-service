package com.task.managment.web.shared.mapper;

import com.task.management.domain.shared.model.UserInfo;
import com.task.managment.web.shared.dto.UserInfoDto;
import org.springframework.stereotype.Component;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

@Component
public class UserInfoMapper {

    public UserInfoDto toDto(UserInfo model) {
        parameterRequired(model, "User info");
        return UserInfoDto.builder()
                .id(model.id().value())
                .email(model.email().value())
                .firstName(model.firstName())
                .lastName(model.lastName())
                .fullName(mapFullName(model))
                .build();
    }

    private String mapFullName(UserInfo model) {
        return "%s %s".formatted(model.firstName(), model.lastName());
    }
}
