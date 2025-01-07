package ru.yandex.practicum.filmorate;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalid-email"); // Некорректный email
        user.setLogin("username");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Некорректный формат email")));
    }

    @Test
    void shouldFailValidationWhenLoginHasSpaces() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user name"); // Логин с пробелами
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Логин не может содержать пробелы")));
    }

    @Test
    void shouldPassValidationWhenAllFieldsAreValid() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("username");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }
}