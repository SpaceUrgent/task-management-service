package com.task.management.application.iam.port.out;

import com.task.management.application.iam.model.User;

public interface AddUserPort {
    User add(User user);
}
