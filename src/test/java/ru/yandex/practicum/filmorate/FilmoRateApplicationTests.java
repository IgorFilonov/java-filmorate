package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate; // Добавляем JdbcTemplate

    @BeforeEach
    void setUp() {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, "test@example.com", "testuser", "Test User", LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testFindUserById() {
        String getIdSql = "SELECT id FROM users WHERE email = ?";
        Integer userId = jdbcTemplate.queryForObject(getIdSql, Integer.class, "test@example.com");

        Optional<User> userOptional = userStorage.findById(userId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user.getId()).isEqualTo(userId)
                );
    }
}