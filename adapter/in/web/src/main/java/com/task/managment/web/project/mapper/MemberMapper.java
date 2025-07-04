package com.task.managment.web.project.mapper;

import com.task.management.application.project.projection.MemberView;
import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.managment.web.project.dto.MemberDto;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberMapper {

    public MemberDto toDto(MemberView model) {
        return MemberDto.builder()
                .id(model.id().value())
                .email(model.email().value())
                .fullName(model.fullName())
                .role(roleName(model.role()))
                .build();
    }

    private String roleName(MemberRole role) {
        return Optional.ofNullable(role).map(MemberRole::roleName).orElse(null);
    }
}
