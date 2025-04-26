package com.task.management.domain.project.port.out;

import com.task.management.domain.common.model.UserId;
import com.task.management.domain.project.model.*;

import java.util.Optional;

public interface MemberRepositoryPort {

    Member save(Member member);

    Optional<Member> find(ProjectId projectId, UserId memberId);
}
