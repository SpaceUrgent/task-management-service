package com.task.management.persistence.jpa.repository;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.application.project.port.out.MemberRepositoryPort;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.domain.project.model.Member;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.persistence.jpa.dao.MemberEntityDao;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.dao.UserEntityDao;
import com.task.management.persistence.jpa.entity.MemberEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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
        projectIdRequired(projectId);
        memberIdRequired(memberId);
        final var memberPK = new MemberEntity.MemberPK(projectId.value(), memberId.value());
        return findMemberEntity(memberPK).map(JpaMemberRepositoryAdapter::toMember);
    }

    @Override
    public void delete(UserId memberId, ProjectId projectId) {
        memberIdRequired(memberId);
        projectIdRequired(projectId);
        memberDao.delete(get(new MemberEntity.MemberPK(projectId.value(), memberId.value())));
    }

    private static Member toMember(MemberEntity memberEntity) {
        return Member.builder()
                .projectId(new ProjectId(memberEntity.getId().getProjectId()))
                .id(new UserId(memberEntity.getId().getMemberId()))
                .role(memberEntity.getRole())
                .build();
    }

    private MemberEntity get(MemberEntity.MemberPK memberPK) {
        return findMemberEntity(memberPK).orElseThrow(EntityNotFoundException::new);
    }

    private Optional<MemberEntity> findMemberEntity(MemberEntity.MemberPK memberPK) {
        return memberDao.findById(memberPK);
    }

    private static void projectIdRequired(ProjectId projectId) {
        parameterRequired(projectId, "Project id");
    }

    private static void memberIdRequired(UserId memberId) {
        parameterRequired(memberId, "Member id");
    }
}
