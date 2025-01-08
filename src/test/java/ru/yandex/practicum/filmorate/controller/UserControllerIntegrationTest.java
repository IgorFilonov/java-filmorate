package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldAddUserSuccessfully() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("username");
        user.setName("User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> response = restTemplate.postForEntity("/users", user, User.class);

        assertEquals(201, response.getStatusCodeValue()); // 201 Created
        assertNotNull(response.getBody());
        assertEquals("user@example.com", response.getBody().getEmail());
    }

    @Test
    void shouldReturnBadRequestForInvalidUser() {
        User user = new User();
        user.setEmail("invalid-email"); // Некорректный email
        user.setLogin("username");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);

        assertEquals(400, response.getStatusCodeValue()); // 400 Bad Request
        assertTrue(response.getBody().contains("Некорректный формат email"));
    }

    @Test
    void shouldReturnBadRequestForInvalidLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user name"); // Логин с пробелами
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<String> response = restTemplate.postForEntity("/users", user, String.class);

        assertEquals(400, response.getStatusCodeValue()); // 400 Bad Request
        assertTrue(response.getBody().contains("Логин не может содержать пробелы"));
    }
}