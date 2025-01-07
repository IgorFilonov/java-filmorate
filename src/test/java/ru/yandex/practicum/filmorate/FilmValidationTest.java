package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenNameIsBlank() {
        Film film = new Film();
        film.setName(""); // Некорректное имя
        film.setDescription("Good movie");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Название не может быть пустым")));
    }

    @Test
    void shouldFailValidationWhenDescriptionTooLong() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("A".repeat(201)); // 201 символ
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Описание не должно превышать 200 символов")));
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        Film film = new Film();
        film.setName("Inception");
        film.setDescription("Great movie.");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }
}