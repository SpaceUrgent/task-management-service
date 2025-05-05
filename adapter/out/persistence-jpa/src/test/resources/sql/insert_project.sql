insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'jdoe@mail.com', 'John', 'Doe', 'encryptedPassword');

insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'bbtornton@mail.com', 'Bill', 'Tornton', 'encryptedPassword');

insert into projects (created_at, title, description)
values (now(), 'Project 1', 'Project description');

insert into available_task_statuses (project_id, name, position)
values ((select id from projects where title = 'Project 1'), 'To do', 1);

insert into available_task_statuses (project_id, name, position)
values ((select id from projects where title = 'Project 1'), 'Done', 2);

insert into projects_members (project_id, member_id, role)
values ((select id from projects where title = 'Project 1'), (select id from users where email = 'jdoe@mail.com'), 'OWNER');

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project 1'), (select id from users where email = 'bbtornton@mail.com'));

insert into task_number_seq(project_id, created_at, current_value)
values ((select id from projects where title = 'Project 1'), now(), 140);

