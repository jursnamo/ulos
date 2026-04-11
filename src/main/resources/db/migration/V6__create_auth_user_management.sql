create table if not exists los_role (
    id bigint not null auto_increment,
    role_code varchar(50) not null,
    role_name varchar(120) not null,
    description varchar(400),
    active_flag bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_role_code (role_code)
);

create table if not exists los_user (
    id bigint not null auto_increment,
    username varchar(80) not null,
    full_name varchar(150) not null,
    email varchar(180),
    password_hash varchar(128) not null,
    active_flag bit not null,
    last_login_at datetime(6),
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_user_username (username)
);

create table if not exists los_user_role (
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id),
    constraint fk_los_user_role_user foreign key (user_id) references los_user (id),
    constraint fk_los_user_role_role foreign key (role_id) references los_role (id)
);

create table if not exists los_auth_token (
    id bigint not null auto_increment,
    token_value varchar(120) not null,
    user_id bigint not null,
    issued_at datetime(6) not null,
    expires_at datetime(6) not null,
    last_accessed_at datetime(6),
    revoked_flag bit not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    primary key (id),
    unique key uk_los_auth_token_value (token_value),
    constraint fk_los_auth_token_user foreign key (user_id) references los_user (id)
);

insert into los_role (role_code, role_name, description, active_flag, created_at, updated_at)
values
    ('ADMIN', 'System Administrator', 'Full access to all LOS modules and BPMN administration.', b'1', current_timestamp(6), current_timestamp(6)),
    ('ANALYST', 'Credit Analyst', 'Portfolio and application preparation, submission, and monitoring.', b'1', current_timestamp(6), current_timestamp(6)),
    ('RISK', 'Risk Reviewer', 'Risk-stage review and task decisioning.', b'1', current_timestamp(6), current_timestamp(6)),
    ('COMMITTEE', 'Credit Committee', 'Committee-stage approval authority.', b'1', current_timestamp(6), current_timestamp(6)),
    ('VIEWER', 'Read Only', 'Read-only access to LOS dashboards and data.', b'1', current_timestamp(6), current_timestamp(6))
on duplicate key update
    role_name = values(role_name),
    description = values(description),
    active_flag = values(active_flag),
    updated_at = values(updated_at);

insert into los_user (username, full_name, email, password_hash, active_flag, created_at, updated_at)
values
    ('admin', 'System Administrator', 'admin@ulos.local', sha2('12345', 256), b'1', current_timestamp(6), current_timestamp(6)),
    ('analyst1', 'Senior Credit Analyst', 'analyst1@ulos.local', sha2('12345', 256), b'1', current_timestamp(6), current_timestamp(6)),
    ('risk1', 'Risk Manager', 'risk1@ulos.local', sha2('12345', 256), b'1', current_timestamp(6), current_timestamp(6)),
    ('committee1', 'Credit Committee Member', 'committee1@ulos.local', sha2('12345', 256), b'1', current_timestamp(6), current_timestamp(6)),
    ('viewer1', 'Portfolio Viewer', 'viewer1@ulos.local', sha2('12345', 256), b'1', current_timestamp(6), current_timestamp(6))
on duplicate key update
    full_name = values(full_name),
    email = values(email),
    active_flag = values(active_flag),
    updated_at = values(updated_at);

insert into los_user_role (user_id, role_id)
select u.id, r.id
from los_user u
join los_role r on r.role_code = 'ADMIN'
where u.username = 'admin'
on duplicate key update user_id = values(user_id);

insert into los_user_role (user_id, role_id)
select u.id, r.id
from los_user u
join los_role r on r.role_code = 'ANALYST'
where u.username = 'analyst1'
on duplicate key update user_id = values(user_id);

insert into los_user_role (user_id, role_id)
select u.id, r.id
from los_user u
join los_role r on r.role_code = 'RISK'
where u.username = 'risk1'
on duplicate key update user_id = values(user_id);

insert into los_user_role (user_id, role_id)
select u.id, r.id
from los_user u
join los_role r on r.role_code = 'COMMITTEE'
where u.username = 'committee1'
on duplicate key update user_id = values(user_id);

insert into los_user_role (user_id, role_id)
select u.id, r.id
from los_user u
join los_role r on r.role_code = 'VIEWER'
where u.username = 'viewer1'
on duplicate key update user_id = values(user_id);
