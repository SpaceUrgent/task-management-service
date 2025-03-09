package com.task.managment.web.iam.mapper;

import com.task.management.domain.iam.model.UserProfile;
import com.task.managment.web.iam.dto.UserProfileDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserProfileDto toDto(UserProfile userProfile) {
        return UserProfileDto.builder()
                .id(userProfile.id().value())
                .email(userProfile.email().value())
                .firstName(userProfile.firstName())
                .lastName(userProfile.lastName())
                .build();
    }
}
