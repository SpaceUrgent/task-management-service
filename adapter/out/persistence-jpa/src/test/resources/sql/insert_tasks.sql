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

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jdoe@mail.com'));

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'jsnow@mail.com'));

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project'), (select id from users where email = 'bbeggins@mail.com'));

-- insert task

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 1', 'Task 1 Description', 'TO_DO',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 2', 'Task 2 Description', 'TO_DO',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 3', 'Task 3 Description', 'TO_DO',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 4', 'Task 4 Description', 'TO_DO',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));


insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 5', 'Task 5 Description', 'TO_DO',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));


insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 6', 'Task 6 Description', 'TO_DO',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 7', 'Task 7 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 8', 'Task 8 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 9', 'Task 9 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 10', 'Task 10 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 11', 'Task 11 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 12', 'Task 12 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 13', 'Task 13 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 14', 'Task 14 Description', 'IN_PROGRESS',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 15', 'Task 15 Description', 'DONE',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 16', 'Task 16 Description', 'DONE',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 17', 'Task 17 Description', 'DONE',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 18', 'Task 18 Description', 'DONE',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'jsnow@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 19', 'Task 19 Description', 'DONE',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));

insert into tasks (created_at, title, description, status, project_id, owner_id, assignee_id)
values (now(), 'Task 20', 'Task 20 Description', 'DONE',
(select id from projects where title = 'Project'),
(select id from users where email = 'jdoe@mail.com'),
(select id from users where email = 'bbeggins@mail.com'));