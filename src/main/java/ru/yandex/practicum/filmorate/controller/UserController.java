package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final List<User> users = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        user.setId(currentId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.add(user);
        log.info("Пользователь добавлен: {}", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user); // Возвращаем статус 201 Created
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@Valid @RequestBody User user) {
        Optional<User> existingUser = users.stream()
                .filter(u -> u.getId() == user.getId())
                .findFirst();

        if (existingUser.isEmpty()) {
            log.warn("Пользователь с ID {} не найден для обновления", user.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Пользователь с ID " + user.getId() + " не найден."));
        }

        users.remove(existingUser.get());
        users.add(user);
        log.info("Пользователь обновлён: {}", user);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Запрос всех пользователей");
        return ResponseEntity.ok(users); // 200 OK
    }
}