package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.query(sql, new GenreMapper(), id)
                .stream().findFirst();
    }

    public Optional<Genre> findGenreById(int id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre, id);
        return genres.stream().findFirst();
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("id"), rs.getString("name"));
    }

    private static class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                    rs.getInt("id"),
                    rs.getString("name")
            );
        }
    }
}
