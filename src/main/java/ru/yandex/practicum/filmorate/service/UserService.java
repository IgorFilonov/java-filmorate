package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Добавление нового пользователя
    public User addUser(User user) {
        return userStorage.add(user);
    }

    // Обновление существующего пользователя
    public User updateUser(User user) {
        userStorage.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userStorage.update(user);
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    // Получение пользователя по ID
    public User getUserById(int id) {
        if (id <= 0) {
            throw new BadRequestException("ID пользователя должен быть положительным");
        }
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    // Добавление друга (односторонняя дружба)
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new BadRequestException("Нельзя добавить себя в друзья");
        }

        // Проверка на существование обоих пользователей
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        userStorage.addFriend(userId, friendId);
    }

    // Удаление друга (односторонняя модель)
    public void removeFriend(int userId, int friendId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userStorage.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        userStorage.removeFriend(userId, friendId);
    }

    // Получение списка друзей пользователя
    public List<User> getFriends(int userId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userStorage.getFriends(userId);
    }

    // Получение списка общих друзей между двумя пользователями
    public List<User> getMutualFriends(int userId, int otherId) {
        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userStorage.findById(otherId)
                .orElseThrow(() -> new NotFoundException("Друг не найден"));

        return userStorage.getCommonFriends(userId, otherId);
    }
}