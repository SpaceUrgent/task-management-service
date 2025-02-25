insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'jdoe@mail.com', 'John', 'Doe', 'encryptedPassword');

insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 1', 'Project description', (select id from users where email = 'jdoe@mail.com'));

insert into projects_members (project_id, member_id)
values ((select id from projects where title = 'Project 1'), (select id from users where email = 'jdoe@mail.com'));