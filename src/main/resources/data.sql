DELETE FROM genres;
INSERT INTO genres (id, name) VALUES (1, 'Комедия');
INSERT INTO genres (id, name) VALUES (2, 'Драма');
INSERT INTO genres (id, name) VALUES (3, 'Мультфильм');
INSERT INTO genres (id, name) VALUES (4, 'Триллер');
INSERT INTO genres (id, name) VALUES (5, 'Документальный');
INSERT INTO genres (id, name) VALUES (6, 'Боевик');

DELETE FROM mpa;
INSERT INTO mpa (id, name) VALUES (1, 'G');
INSERT INTO mpa (id, name) VALUES (2, 'PG');
INSERT INTO mpa (id, name) VALUES (3, 'PG-13');
INSERT INTO mpa (id, name) VALUES (4, 'R');
INSERT INTO mpa (id, name) VALUES (5, 'NC-17');

DELETE FROM films;
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('Inception', 'Sci-Fi thriller', '2010-07-16', 148, 4);

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('The Matrix', 'Cyberpunk action', '1999-03-31', 136, 4);