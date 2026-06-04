insert into authors(full_name)
values ('Mikhail Bulgakov'), ('Alexander Pushkin'), ('Nikolai Gogol')
on conflict(id) do nothing;

insert into genres(name)
values ('Mysticism'), ('Satire'), ('Russian classics'),
       ('Fantasy'), ('Detective'), ('Classics')
on conflict(id) do nothing;

insert into books(title, author_id)
values ('The Master and Margarita', 1), ('The Eugene Onegin', 2), ('The Dead Souls', 3)
on conflict(id) do nothing;

insert into books_genres(book_id, genre_id)
values (1, 1),   (1, 2),
       (2, 3),
       (3, 6)
on conflict(book_id, genre_id) do nothing;

insert into comments(text, book_id)
values ('Good book', 1), ('Wonderful', 2), ('Nice book', 3)
on conflict(id) do nothing;