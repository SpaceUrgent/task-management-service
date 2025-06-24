package com.task.management.application.project.port.out;

import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.Member;
import com.task.management.domain.shared.model.objectvalue.ProjectId;

import java.util.Optional;

public interface MemberRepositoryPort {

    Member save(Member member);

    Optional<Member> find(ProjectId projectId, UserId memberId);

    void delete(UserId memberId, ProjectId projectId);
}
