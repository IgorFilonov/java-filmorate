package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final List<Film> films = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        film.setId(currentId++);
        films.add(film);
        log.info("Фильм добавлен: {}", film);
        return ResponseEntity.status(HttpStatus.CREATED).body(film); // Возвращаем статус 201 Created
    }

    @PutMapping
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film film) {
        Optional<Film> existingFilm = films.stream()
                .filter(f -> f.getId() == film.getId())
                .findFirst();

        if (existingFilm.isEmpty()) {
            log.warn("Фильм с ID {} не найден для обновления", film.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Фильм с ID " + film.getId() + " не найден."));
        }

        films.remove(existingFilm.get());
        films.add(film);
        log.info("Фильм обновлён: {}", film);
        return ResponseEntity.ok(film);
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.info("Запрос всех фильмов");
        return ResponseEntity.ok(films); // 200 OK
    }
}