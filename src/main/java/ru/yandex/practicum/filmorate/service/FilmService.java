package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaService mpaService;
    private final GenreService genreService; // Добавили GenreService

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            MpaService mpaService,
            GenreService genreService // Внедряем GenreService
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
    }

    public Film addFilm(Film film) {
        mpaService.getMpaById(film.getMpa().getId());
        genreService.validateGenresExist(film.getGenres()); // Проверка жанров
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        // Если фильм не найден, выбрасываем исключение
        filmStorage.findById(film.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден"));

        return filmStorage.update(film);
    }

    public Film getFilmById(int id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с ID " + id + " не найден"));
    }

    public List<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public void addLike(int filmId, int userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID фильма или пользователя должен быть положительным");
        }

        // Проверяем, существует ли фильм и пользователь
        filmStorage.findById(filmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден"));

        userStorage.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        // Добавляем лайк в БД через `filmStorage`
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(int filmId, int userId) {
        if (userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID пользователя должен быть положительным");
        }

        // Проверяем, существует ли фильм и пользователь
        filmStorage.findById(filmId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден"));

        userStorage.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));

        // Удаляем лайк через `filmStorage`
        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findPopularFilms(count);
    }
}