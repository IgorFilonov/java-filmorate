package ru.yandex.practicum.filmorate.storage.film;


import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film add(Film film); // Добавление фильма

    Film update(Film film); // Обновление фильма

    void delete(int id); // Удаление фильма

    Optional<Film> findById(int id); // Поиск фильма по ID

    List<Film> findAll(); // Получение всех фильмов

    void addLike(int filmId, int userId); // Добавление лайка

    void removeLike(int filmId, int userId); // Удаление лайка

    List<Film> findPopularFilms(int count); // Получение популярных фильмов
}
