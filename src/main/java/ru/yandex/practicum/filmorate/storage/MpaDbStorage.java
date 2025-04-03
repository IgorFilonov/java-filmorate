package ru.yandex.practicum.filmorate.storage;

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
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
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