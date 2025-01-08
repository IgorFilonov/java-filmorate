package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAddFilmSuccessfully() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("Great movie.");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        ResponseEntity<Film> response = restTemplate.postForEntity("/films", film, Film.class);

        assertEquals(201, response.getStatusCodeValue()); // 201 Created
        assertNotNull(response.getBody());
        assertEquals("Inception", response.getBody().getName());
    }

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
}