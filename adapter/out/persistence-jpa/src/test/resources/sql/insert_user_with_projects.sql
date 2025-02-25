-- insert test users
insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'owner@mail.com', 'John', 'Owner', 'encryptedPassword');

insert into users (created_at, email, first_name, last_name, encrypted_password)
values (now(), 'member@mail.com', 'Bob', 'Member', 'encryptedPassword');

-- insert projects

insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 1', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 2', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 3', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 4', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 5', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 6', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 7', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 8', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 9', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 10', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 11', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 12', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 13', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 14', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 15', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 16', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 17', 'Project with test member', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 18', 'Random project', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 19', 'Random project', (select id from users where email = 'owner@mail.com'));
insert into projects (created_at, title, description, owner_id)
values (now(), 'Project 20', 'Random project', (select id from users where email = 'owner@mail.com'));

-- Add to all projects member with email 'member@mail.com'

insert into projects_members (project_id, member_id)
select p.id, u.id
from projects p
cross join (select id from users where email = 'member@mail.com') u
where p.description = 'Project with test member' and not exists (
    select 1
    from projects_members pm
    where pm.project_id = p.id and pm.member_id = u.id
);

