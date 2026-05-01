create table if not exists authors (
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

create table if not exists genres (
    id bigserial,
    name varchar(255),
    primary key (id)
);

create table if not exists books (
    id bigserial,
    title varchar(255),
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);

create table if not exists books_genres (
    book_id bigint references books(id) on delete cascade,
    genre_id bigint references genres(id) on delete cascade,
    primary key (book_id, genre_id)
);

create table if not exists comments (
    id bigserial,
    text varchar(500),
    book_id bigint references books (id) on delete cascade,
    primary key (id)
);

create table if not exists users (
    id bigserial,
    username varchar(255),
    password varchar(255),
    primary key (id)
);

create table if not exists roles (
    id bigserial,
    name varchar(255),
    primary key (id)
);

create table if not exists users_roles (
    user_id bigint references users(id) on delete cascade,
    role_id bigint references roles(id) on delete cascade,
    primary key (user_id, role_id)
);

--acl--
create table if not exists acl_sid (
    id        bigserial primary key,
    principal boolean      not null,
    sid       varchar(100) not null,
    constraint unique_uk_1 unique (sid, principal)
);

create table if not exists acl_class (
    id    bigserial primary key,
    class varchar(255) not null,
    constraint unique_uk_2 unique (class)
);

create table if not exists acl_entry (
    id                  bigserial primary key,
    acl_object_identity bigint  not null,
    ace_order           int     not null,
    sid                 bigint  not null,
    mask                int     not null,
    granting            boolean not null,
    audit_success       boolean not null,
    audit_failure       boolean not null,
    constraint unique_uk_3 unique (acl_object_identity, ace_order)
);

create table if not exists acl_object_identity (
    id                 bigserial primary key,
    object_id_class    bigint  not null,
    object_id_identity bigint  not null,
    parent_object      bigint,
    owner_sid          bigint,
    entries_inheriting boolean not null,
    constraint unique_uk_4 unique (object_id_class, object_id_identity)
);

alter table acl_entry add foreign key (acl_object_identity) references acl_object_identity(id);
alter table acl_entry add foreign key (sid) references acl_sid(id);

-- Constraints for table acl_object_identity
alter table acl_object_identity add foreign key (parent_object) references acl_object_identity (id);
alter table acl_object_identity add foreign key (object_id_class) references acl_class (id);
alter table acl_object_identity add foreign key (owner_sid) references acl_sid (id);
