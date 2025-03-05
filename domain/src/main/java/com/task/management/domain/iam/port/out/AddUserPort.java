package com.task.management.domain.iam.port.out;

import com.task.management.domain.iam.model.User;

public interface AddUserPort {
    User add(User user);
}
