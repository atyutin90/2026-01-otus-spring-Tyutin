insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3'),
       ('Genre_4'), ('Genre_5'), ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),   (2, 4),
       (3, 5),   (3, 6);

insert into comments(text, book_id)
values ('Comment_1', 1), ('Comment_2', 2), ('Comment_3', 3);

insert into users(username, password)
values ('admin', '$2a$10$U6O4LSdtBBjbi2uHJlToNOG5VIdbH34nV/oeb5fF/NK/5o1gP9lZm'),
       ('user', '$2a$10$gq2NRIXcXFbo97vh93rT2OFBSKoeLPSYAisd8Y9yoPqqihUNQKdk2');

insert into roles(id, name)
values (1, 'USER'), (2, 'ADMIN');

insert into users_roles(user_id, role_id)
values (1, 1),
       (1, 2),
       (2, 1);
