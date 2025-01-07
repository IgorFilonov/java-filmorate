package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final List<User> users = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        user.setId(currentId++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.add(user);
        log.info("Пользователь добавлен: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        users.replaceAll(u -> u.getId() == user.getId() ? user : u);
        log.info("Пользователь обновлён: {}", user);
        return user;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос всех пользователей");
        return users;
    }
}