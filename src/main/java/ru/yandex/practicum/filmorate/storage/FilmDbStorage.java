package ru.yandex.practicum.filmorate.storage;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


@Repository
@Primary
@Qualifier("filmDbStorage")  // Должно совпадать с @Qualifier в FilmService
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    // Создание фильма
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, film.getReleaseDate() != null ? Date.valueOf(film.getReleaseDate()) : null);
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpa() != null ? film.getMpa().getId() : null, Types.INTEGER);
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            film.setId(keyHolder.getKey().intValue());
        }

        // Сохраняем жанры
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        // Обновляем MPA и жанры с названиями
        if (film.getMpa() != null) {
            film.setMpa(getMpaById(film.getMpa().getId()));
        }
        film.setGenres(getGenresByFilmId(film.getId()));

        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        // Проверяем, есть ли фильм
        String filmCheckSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer filmCount = jdbcTemplate.queryForObject(filmCheckSql, Integer.class, filmId);

        // Проверяем, есть ли пользователь
        String userCheckSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(userCheckSql, Integer.class, userId);

        // Логируем, что передается в запрос
        log.info("Параметр userId: {}", userId);

        // Если фильм или пользователь не найдены - кидаем исключение
        if (filmCount == null || filmCount == 0) {
            throw new IllegalArgumentException("Фильм с id " + filmId + " не найден.");
        }
        if (userCount == null || userCount == 0) {
            throw new IllegalArgumentException("Пользователь с id " + userId + " не найден.");
        }

        // Добавляем лайк
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        // Проверяем, существует ли фильм
        String filmCheckSql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer filmCount = jdbcTemplate.queryForObject(filmCheckSql, Integer.class, filmId);

        // Проверяем, существует ли пользователь
        String userCheckSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        Integer userCount = jdbcTemplate.queryForObject(userCheckSql, Integer.class, userId);

        // Если фильм или пользователь не найдены - кидаем исключение
        if (filmCount == null || filmCount == 0) {
            throw new IllegalArgumentException("Фильм с id " + filmId + " не найден.");
        }
        if (userCount == null || userCount == 0) {
            throw new IllegalArgumentException("Пользователь с id " + userId + " не найден.");
        }

        // Удаляем лайк
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId, userId);

        if (rowsAffected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Лайк не найден для фильма с id " + filmId + " и пользователя с id " + userId);
        }
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        String sql = String.format("""
                        SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, m.name AS mpa_name, COUNT(fl.user_id) AS likes
                            FROM films f
                            LEFT JOIN film_likes fl ON f.id = fl.film_id
                            LEFT JOIN mpa m ON f.mpa_id = m.id  -- Добавляем связь с таблицей mpa
                            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id, mpa_name
                            ORDER BY likes DESC
                            LIMIT %d
                """, count);

        return jdbcTemplate.query(sql, new FilmMapper(this));
    }

    // Обновление фильма
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );

        // Удаляем старые жанры
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        // Добавляем новые жанры
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId());
            }
        }

        // Обновляем и возвращаем полные данные
        if (film.getMpa() != null) {
            film.setMpa(getMpaById(film.getMpa().getId()));
        }
        film.setGenres(getGenresByFilmId(film.getId()));

        return film;
    }

    // Получение фильма по ID
    public Optional<Film> findById(int id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f " +
                "LEFT JOIN mpa m ON f.mpa_id = m.id " +
                "WHERE f.id = ?";

        try {
            List<Film> films = jdbcTemplate.query(sql, new FilmMapper(this), id);
            return films.stream().findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении фильма с ID = " + id, e);
        }
    }

    // Получение всех фильмов
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.id";
        return jdbcTemplate.query(sql, new FilmMapper(this));
    }

    // Удаление фильма
    public void delete(int id) {
        String sql = "DELETE FROM films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Получение MPA по ID
    private Mpa getMpaById(int mpaId) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,
                (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name")), mpaId);
    }

    // Получение жанров по ID фильма
    private Set<Genre> getGenresByFilmId(int filmId) {
        String sql = "SELECT g.id, g.name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ? ORDER BY g.id";  // Сортировка по ID жанра

        return new LinkedHashSet<>(jdbcTemplate.query(sql,
                (rs, rowNum) -> new Genre(rs.getInt("id"), rs.getString("name")), filmId));
    }

    // Маппер для фильма
    private static class FilmMapper implements RowMapper<Film> {
        private final FilmDbStorage storage;

        public FilmMapper(FilmDbStorage storage) {
            this.storage = storage;
        }

        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            int filmId = rs.getInt("id");

            Date sqlDate = rs.getDate("release_date");
            LocalDate releaseDate = (sqlDate != null) ? sqlDate.toLocalDate() : null;

            int mpaId = rs.getInt("mpa_id");
            String mpaName = rs.getString("mpa_name");
            Mpa mpa = (mpaId > 0 && mpaName != null) ? new Mpa(mpaId, mpaName) : null;

            return new Film(
                    filmId,
                    rs.getString("name"),
                    rs.getString("description"),
                    releaseDate,
                    rs.getInt("duration"),
                    mpa,
                    storage.getGenresByFilmId(filmId)
            );
        }
    }
}