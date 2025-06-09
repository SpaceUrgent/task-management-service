-- insert users
insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'jdoe@mail.com', 'John', 'Doe', 'encryptedPassword');

insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'jsnow@mail.com', 'John', 'Snow', 'encryptedPassword');


insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'bbeggins@mail.com', 'Bilbo', 'Beggins', 'encryptedPassword');

-- insert project and project members

insert into projects (created_at, title, description)
values (now(), 'Project', 'Project description');

insert into task_number_seq(project_id, created_at, current_value)
values ((select id from projects where title = 'Project'), now(), 0);

insert into task_statuses (project_id, name, position, is_final)
values ((select id from projects where title = 'Project'), 'To do', 1, 'false');

insert into task_statuses (project_id, name, position, is_final)
values ((select id from projects where title = 'Project'), 'Done', 2, 'false');

insert into projects_members (project_id, member_id, role)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jdoe@mail.com'), 'OWNER');

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jsnow@mail.com'));

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'bbeggins@mail.com'));

-- insert task

insert into tasks (created_at, due_date, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (
        now(),
        '2050-05-31',
        1,
        'New task',
        'New task description',
        'To do',
        2,
        (select id from projects where title = 'Project'),
        (select id from users where email = 'jdoe@mail.com'),
        (select id from users where email = 'jsnow@mail.com')
);

insert into task_change_logs (created_at, occurred_at, task_id, actor_id, field_changed, old_value, new_value)
values (
        now(),
        now(),
        (select id from tasks where title = 'New task'),
        (select id from users where email = 'jsnow@mail.com'),
        'TITLE',
        'Old title',
        'New title'
);

insert into task_comments (created_at, task_id, author_id, content)
values (
        now(),
        (select id from tasks where title = 'New task'),
        (select id from users where email = 'jsnow@mail.com'),
        'Please, help to solve this!'
)