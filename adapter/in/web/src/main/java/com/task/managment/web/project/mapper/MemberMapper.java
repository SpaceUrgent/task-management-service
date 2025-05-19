package com.task.managment.web.project.mapper;

import com.task.management.application.project.projection.MemberView;
import com.task.managment.web.project.dto.MemberDto;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public MemberDto toDto(MemberView model) {
        return MemberDto.builder()
                .id(model.id().value())
                .email(model.email().value())
                .fullName(model.fullName())
                .role(model.role().roleName())
                .build();
    }
}
