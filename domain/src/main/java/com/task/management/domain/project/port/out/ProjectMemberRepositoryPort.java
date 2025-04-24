package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.*;

import java.util.Optional;

public interface ProjectMemberRepositoryPort {

    Optional<Member> findMember(ProjectId projectId, MemberId memberId);

    Member update(Member member);
}
