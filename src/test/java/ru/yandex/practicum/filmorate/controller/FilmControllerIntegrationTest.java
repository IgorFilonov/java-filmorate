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

        assertEquals(200, response.getStatusCodeValue());
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

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Название не может быть пустым"));
    }
}