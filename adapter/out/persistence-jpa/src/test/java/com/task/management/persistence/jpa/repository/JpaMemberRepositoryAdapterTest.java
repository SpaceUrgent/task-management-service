package com.task.management.persistence.jpa.repository;

import com.task.management.domain.project.model.objectvalue.MemberRole;
import com.task.management.domain.shared.model.objectvalue.ProjectId;
import com.task.management.domain.shared.model.objectvalue.UserId;
import com.task.management.persistence.jpa.InvalidTestSetupException;
import com.task.management.persistence.jpa.PersistenceTest;
import com.task.management.persistence.jpa.dao.MemberEntityDao;
import com.task.management.persistence.jpa.dao.ProjectEntityDao;
import com.task.management.persistence.jpa.entity.MemberEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@Sql(
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        scripts = "classpath:sql/clear.sql"
)
@PersistenceTest
class JpaMemberRepositoryAdapterTest {
    @Autowired
    private MemberEntityDao memberDao;
    @Autowired
    private ProjectEntityDao projectEntityDao;
    @Autowired
    private JpaMemberRepositoryAdapter jpaMemberRepository;

    @Sql(
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
            scripts = {
                    "classpath:sql/insert_project.sql"
            }
    )
    @Test
    void delete() {
        final var projectEntity = projectEntityDao.findAll().stream()
                .filter(project -> project.getMembers().size() > 1)
                .max(Comparator.comparing(project -> project.getMembers().size()))
                .orElseThrow(() -> new InvalidTestSetupException("Project with more than 1 member expected in DB"));
        final var givenMemberId = projectEntity.getMembers().stream()
                .filter(memberEntity -> MemberRole.OWNER != memberEntity.getRole())
                .findFirst()
                .map(MemberEntity::getId)
                .map(MemberEntity.MemberPK::getMemberId)
                .orElseThrow(() -> new InvalidTestSetupException("Invalid project state, expected member without OWNER role"));
        jpaMemberRepository.delete(new UserId(givenMemberId), new ProjectId(projectEntity.getId()));
        assertTrue(memberDao.findById(new MemberEntity.MemberPK(projectEntity.getId(), givenMemberId)).isEmpty());
    }
}