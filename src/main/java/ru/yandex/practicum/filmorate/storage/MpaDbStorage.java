package ru.yandex.practicum.filmorate.storage;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Mpa;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa ORDER BY id"; // Теперь сортируем по ID
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private Mpa getMpaById(int mpaId) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> new Mpa(rs.getInt("id"), rs.getString("name")), mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MPA рейтинг не найден: id=" + mpaId);
        }
    }

    public Optional<Mpa> findMpaById(int id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        return mpaList.stream().findFirst();
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("name"));
    }
}