package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDbStorage {
    private final JdbcTemplate jdbcTemplate;

    // Создание пользователя
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    // Получение пользователя по ID
    public Optional<User> findUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, new UserMapper())
                .stream().findFirst();
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserMapper());
    }

    // Обновление пользователя
    public void updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }

    // Удаление пользователя
    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, id);
    }

    // Маппер для преобразования строки результата в объект User
    private static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getInt("user_id"));
            user.setEmail(rs.getString("email"));
            user.setLogin(rs.getString("login"));
            user.setName(rs.getString("name"));
            user.setBirthday(rs.getDate("birthday").toLocalDate());
            return user;
        }
    }
}