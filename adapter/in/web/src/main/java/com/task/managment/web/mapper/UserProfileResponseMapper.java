package com.task.managment.web.mapper;

import com.task.management.domain.iam.model.UserProfile;
import com.task.managment.web.dto.UserProfileDto;
import org.springframework.stereotype.Component;

@Component
public class UserProfileResponseMapper {

    public UserProfileDto toResponse(UserProfile userProfile) {
        return UserProfileDto.builder()
                .id(userProfile.id().value())
                .email(userProfile.email())
                .firstName(userProfile.firstName())
                .lastName(userProfile.lastName())
                .build();
    }
}
