-- insert users
insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'jdoe@mail.com', 'John', 'Doe', 'encryptedPassword');

insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'jsnow@mail.com', 'John', 'Snow', 'encryptedPassword');


insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'bbeggins@mail.com', 'Bilbo', 'Beggins', 'encryptedPassword');

-- insert project and project members

insert into projects (created_at, title, description, owner_id)
values (now(), 'Project', 'Project description', (select id from users where email = 'jdoe@mail.com'));

insert into task_number_seq(project_id, created_at, current_value)
values ((select id from projects where title = 'Project'), now(), 0);

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jdoe@mail.com'));

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jsnow@mail.com'));

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'bbeggins@mail.com'));

-- insert task

insert into tasks (created_at, number, title, description, status, project_id, owner_id, assignee_id)
values (
        now(),
        1,
        'New task',
        'New task description',
        'TO_DO',
        (select id from projects where title = 'Project'),
        (select id from users where email = 'jdoe@mail.com'),
        (select id from users where email = 'jsnow@mail.com'));
