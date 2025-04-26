package com.task.management.domain.project.port.out;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.*;

import java.util.Optional;

public interface ProjectMemberRepositoryPort {

    Optional<Member> findMember(ProjectId projectId, UserId memberId);

    Member update(Member member);
}
