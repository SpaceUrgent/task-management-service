package com.task.management.domain.project.model.objectvalue;

import com.task.management.domain.shared.validation.ValidationException;

import java.util.Arrays;
import java.util.Objects;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public enum MemberRole {
    OWNER("Owner"),
    ADMIN("Admin");

    private final String roleName;

    MemberRole(String roleName) {
        this.roleName = roleName;
    }

    public String roleName() {
        return this.roleName;
    }

    public static MemberRole withRoleName(String roleName) {
        if (roleName == null) return null;
        return Arrays.stream(MemberRole.values())
                .filter(memberRole -> Objects.equals(memberRole.roleName, roleName))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Illegal member role name '%s' argument value"));
    }
}
