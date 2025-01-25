package com.task.management.application.port.out;

import com.task.management.application.common.Page;
import com.task.management.application.model.Project;
import com.task.management.application.model.UserId;

import java.util.List;

public interface FindProjectsByMemberPort {
    List<Project> findProjectsByMember(UserId member, Page page);
}
