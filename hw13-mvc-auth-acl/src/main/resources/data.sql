insert into authors(full_name)
values ('Mikhail Bulgakov'), ('Alexander Pushkin'), ('Nikolai Gogol');

insert into genres(name)
values ('Mysticism'), ('Satire'), ('Russian classics'),
       ('Fantasy'), ('Detective'), ('Classics');

insert into books(title, author_id)
values ('The Master and Margarita', 1), ('The Eugene Onegin', 2), ('The Dead Souls', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),
       (3, 6);

insert into comments(text, book_id)
values ('Good book', 1), ('Wonderful', 2), ('Nice book', 3);

insert into roles(id, name)
values (1, 'USER'), (2, 'ADMIN');

insert into users(id, username, password)
values (1, 'admin', '$2a$10$tvuUJd3FwkSPz9GjKAijvurtskyZvPw4SbJObHc1H/FZdlcM0llbm'),
       (2, 'user', '$2a$10$5hctq5hCSG6HCqcqigpdBOXwkxUlFThVLRccWc.kIlmSI7a3VTZYS');

insert into users_roles(user_id, role_id)
values (1, 2), (2, 1);

--acl--
insert into acl_sid (id, principal, sid)
values (1, 1, 'admin'),
       (2, 1, 'user'),
       (3, 0, 'ROLE_ADMIN');

insert into acl_class (id, class)
values (1, 'ru.otus.hw.models.Author'),
       (2, 'ru.otus.hw.models.Genre'),
       (3, 'ru.otus.hw.models.Book'),
       (4, 'ru.otus.hw.models.Comment');

insert into acl_object_identity (id, object_id_class, object_id_identity, parent_object, owner_sid, entries_inheriting)
values (1, 1, 1, null, 3, 0), --Author id=1
       (2, 1, 2, null, 3, 0), --Author id=2
       (3, 1, 3, null, 3, 0), --Author id=3
       (4, 2, 1, null, 3, 0), --Genre id=1
       (5, 2, 2, null, 3, 0), --Genre id=2
       (6, 2, 3, null, 3, 0), --Genre id=3
       (7, 2, 4, null, 3, 0), --Genre id=4
       (8, 2, 5, null, 3, 0), --Genre id=5
       (9, 2, 6, null, 3, 0), --Genre id=6
       (10, 3, 1, null, 3, 0), --Book id=1
       (11, 3, 2, null, 3, 0), --Book id=2
       (12, 3, 3, null, 3, 0), --Book id=3
       (13, 4, 1, null, 3, 0), --Comment id=1
       (14, 4, 2, null, 3, 0), --Comment id=2
       (15, 4, 3, null, 3, 0); --Comment id=3

insert into acl_entry (id, acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
values (1, 1, 1, 3, 2, 1, 1, 1), --Role ADMIN-Author id=1-write
       (2, 1, 2, 3, 8, 1, 1, 1), --Role ADMIN-Author id=1-delete
       (3, 2, 1, 3, 2, 1, 1, 1), --Role ADMIN-Author id=2-write
       (4, 2, 2, 3, 8, 1, 1, 1), --Role ADMIN-Author id=2-delete
       (5, 3, 1, 3, 2, 1, 1, 1), --Role ADMIN-Author id=3-write
       (6, 3, 2, 3, 8, 1, 1, 1), --Role ADMIN-Author id=3-delete

       (7, 4, 1, 3, 2, 1, 1, 1), --Role ADMIN-Genre id=1-write
       (8, 4, 2, 3, 8, 1, 1, 1), --Role ADMIN-Genre id=1-delete
       (9, 5, 1, 3, 2, 1, 1, 1), --Role ADMIN-Genre id=2-write
       (10, 5, 2, 3, 8, 1, 1, 1), --Role ADMIN-Genre id=2-delete
       (11, 6, 1, 3, 2, 1, 1, 1), --Role ADMIN-Genre id=3-write
       (12, 6, 2, 3, 8, 1, 1, 1), --Role ADMIN-Genre id=3-delete
       (13, 7, 1, 3, 2, 1, 1, 1), --Role ADMIN-Genre id=4-write
       (14, 7, 2, 3, 8, 1, 1, 1), --Role ADMIN-Genre id=4-delete
       (15, 8, 1, 3, 2, 1, 1, 1), --Role ADMIN-Genre id=5-write
       (16, 8, 2, 3, 8, 1, 1, 1), --Role ADMIN-Genre id=5-delete
       (17, 9, 1, 3, 2, 1, 1, 1), --Role ADMIN-Genre id=6-write
       (18, 9, 2, 3, 8, 1, 1, 1), --Role ADMIN-Genre id=6-delete

       (19, 10, 1, 3, 2, 1, 1, 1), --Role ADMIN-Book id=1-write
       (20, 10, 2, 3, 8, 1, 1, 1), --Role ADMIN-Book id=1-delete
       (21, 11, 1, 3, 2, 1, 1, 1), --Role ADMIN-Book id=2-write
       (22, 11, 2, 3, 8, 1, 1, 1), --Role ADMIN-Book id=2-delete
       (23, 12, 1, 3, 2, 1, 1, 1), --Role ADMIN-Book id=3-write
       (24, 12, 2, 3, 8, 1, 1, 1), --Role ADMIN-Book id=3-delete

       (25, 13, 1, 3, 2, 1, 1, 1), --Role ADMIN-Comment id=1-write
       (26, 13, 2, 3, 8, 1, 1, 1), --Role ADMIN-Comment id=1-delete
       (27, 14, 1, 3, 2, 1, 1, 1), --Role ADMIN-Comment id=2-write
       (28, 14, 2, 3, 8, 1, 1, 1), --Role ADMIN-Comment id=2-delete
       (29, 15, 1, 3, 2, 1, 1, 1), --Role ADMIN-Comment id=3-write
       (30, 15, 2, 3, 8, 1, 1, 1); --Role ADMIN-Comment id=3-delete

alter table acl_sid  alter column id  restart with (select coalesce(max(id), 0) + 1 from acl_sid);

alter table acl_class  alter column id  restart with (select coalesce(max(id), 0) + 1 from acl_class);

alter table acl_object_identity  alter column id restart with (select coalesce(max(id), 0) + 1 from acl_object_identity);

alter table acl_entry  alter column id restart with (select coalesce(max(id), 0) + 1 from acl_entry);