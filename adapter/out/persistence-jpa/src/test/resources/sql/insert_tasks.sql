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

insert into task_number_seq (project_id, created_at, current_value)
values ((select id from projects where title = 'Project'), now(), 0);

insert into projects_members (project_id, member_id, role)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jdoe@mail.com'), 'OWNER');

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jsnow@mail.com'));

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'bbeggins@mail.com'));

-- insert task

insert into tasks (created_at, due_date, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), '2060-11-20', 1, 'Task 1', 'Task 1 Description', 'TO_DO', 0,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 2, 'Task 2', 'Task 2 Description', 'TO_DO', 2,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 3, 'Task 3', 'Task 3 Description', 'TO_DO', 1,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 4, 'Task 4', 'Task 4 Description', 'TO_DO', 1,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));


insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 5, 'Task 5', 'Task 5 Description', 'TO_DO', 3,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));


insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 6, 'Task 6', 'Task 6 Description', 'TO_DO', 4,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 7, 'Task 7', 'Task 7 Description', 'IN_PROGRESS', 4,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 8, 'Task 8', 'Task 8 Description', 'IN_PROGRESS', 2,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 9, 'Task 9', 'Task 9 Description', 'IN_PROGRESS', 4,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 10, 'Task 10', 'Task 10 Description', 'IN_PROGRESS', 4,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 11, 'Task 11', 'Task 11 Description', 'IN_PROGRESS', 4,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 12, 'Task 12', 'Task 12 Description', 'IN_PROGRESS', 3,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 13, 'Task 13', 'Task 13 Description', 'IN_PROGRESS', 3,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 14, 'Task 14', 'Task 14 Description', 'IN_PROGRESS', 3,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 15, 'Task 15', 'Task 15 Description', 'DONE', 2,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 16, 'Task 16', 'Task 16 Description', 'DONE', 2,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 17, 'Task 17', 'Task 17 Description', 'DONE', 4,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 18, 'Task 18', 'Task 18 Description', 'DONE', 3,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 19, 'Task 19', 'Task 19 Description', 'DONE', 3,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, number, title, description, status, priority, project_id, owner_id, assignee_id)
values (now(), 20, 'Task 20', 'Task 20 Description', 'DONE', 2,
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));