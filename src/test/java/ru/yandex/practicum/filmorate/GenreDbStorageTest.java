package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {GenreDbStorage.class})// Используем реальную БД
public class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Autowired
    public GenreDbStorageTest(JdbcTemplate jdbcTemplate) {
        this.genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }

    @Test
    public void testFindGenreById() {
        assertThat(genreDbStorage.getGenreById(1)).isPresent();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void checkData() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM genres");
        System.out.println("Содержимое genres: " + rows);
    }


}