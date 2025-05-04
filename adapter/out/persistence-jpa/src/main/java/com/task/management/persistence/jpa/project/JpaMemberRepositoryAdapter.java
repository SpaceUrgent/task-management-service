package com.task.management.persistence.jpa.project;

import com.task.management.application.common.annotation.AppComponent;
import com.task.management.application.project.port.out.MemberRepositoryPort;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.Member;
import com.task.management.domain.project.model.objectvalue.ProjectId;
import com.task.management.persistence.jpa.dao.MemberEntityDao;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.MemberEntity;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
@RequiredArgsConstructor
public class JpaMemberRepositoryAdapter implements MemberRepositoryPort {
    private final MemberEntityDao memberDao;
    private final UserEntityDao userEntityDao;
    private final ProjectEntityDao projectEntityDao;

    @Override
    public Member save(Member member) {
        parameterRequired(member, "Member");
        var memberEntity = MemberEntity.builder()
                .userEntity(userEntityDao.getReference(member.getId().value()))
                .projectEntity(projectEntityDao.getReference(member.getProjectId().value()))
                .role(member.getRole())
                .build();
        memberEntity = memberDao.save(memberEntity);
        return toMember(memberEntity);
    }

    @Override
    public Optional<Member> find(ProjectId projectId, UserId memberId) {
        parameterRequired(projectId, "Project id");
        parameterRequired(memberId, "Member id");
        final var memberPK = new MemberEntity.MemberPK(projectId.value(), memberId.value());
        return memberDao.findById(memberPK).map(JpaMemberRepositoryAdapter::toMember);
    }

    private static Member toMember(MemberEntity memberEntity) {
        return Member.builder()
                .projectId(new ProjectId(memberEntity.getId().getProjectId()))
                .id(new UserId(memberEntity.getId().getMemberId()))
                .role(memberEntity.getRole())
                .build();
    }
}
