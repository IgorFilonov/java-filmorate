package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage {
    private final JdbcTemplate jdbcTemplate;

    // Создание фильма
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        return film;
    }

    // Получение фильма по ID
    public Optional<Film> findFilmById(int id) {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id WHERE film_id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, new FilmMapper()).stream().findFirst();
    }

    // Получение всех фильмов
    public List<Film> getAllFilms() {
        String sql = "SELECT f.*, m.name as mpa_name FROM films f JOIN mpa m ON f.mpa_id = m.mpa_id";
        return jdbcTemplate.query(sql, new FilmMapper());
    }

    // Обновление фильма
    public void updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
    }

    // Удаление фильма
    public void deleteFilm(int id) {
        String sql = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Маппер для преобразования строки результата в объект Film
    private static class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            int mpaId = rs.getInt("mpa_id");
            String mpaName = rs.getString("mpa_name");

            Mpa mpa = (rs.wasNull()) ? null : new Mpa(mpaId, mpaName);

            return new Film(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    mpa,
                    new HashSet<>() // Жанры будут загружаться отдельно
            );
        }
    }
}