package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;



    @Test
    void shouldReturnBadRequestForInvalidFilm() {
        Film film = new Film();
        film.setName(""); // Пустое название
        film.setDescription("Description.");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);

        assertEquals(400, response.getStatusCodeValue()); // 400 Bad Request
        assertTrue(response.getBody().contains("Название не может быть пустым"));
    }

    @Test
    void shouldReturnBadRequestForInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Old Film");
        film.setDescription("Description.");
        film.setReleaseDate(LocalDate.of(1800, 1, 1)); // Дата до 28 декабря 1895
        film.setDuration(120);

        ResponseEntity<String> response = restTemplate.postForEntity("/films", film, String.class);

        assertEquals(400, response.getStatusCodeValue()); // 400 Bad Request
        assertTrue(response.getBody().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }
    @Test
    public void testFilmFields() {
        // Запрос на добавление фильма
        Film film = new Film();
        film.setName("New Movie");
        film.setDescription("Movie description");
        film.setReleaseDate(LocalDate.of(2023, 4, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1, "PG"));

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        // Проверка, что возвращаемый фильм содержит все необходимые поля
        Film returnedFilm = response.getBody();
        assertNotNull(returnedFilm);
        assertEquals("New Movie", returnedFilm.getName());
        assertEquals("Movie description", returnedFilm.getDescription());
        assertNotNull(returnedFilm.getId()); // Убедитесь, что id сгенерирован
        assertEquals(120, returnedFilm.getDuration());
    }

}