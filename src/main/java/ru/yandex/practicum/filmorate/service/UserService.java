package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        if (!userStorage.findById(user.getId()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " не найден");
        }
        return userStorage.update(user);
    }

    // Получение всех пользователей
    public List<User> getAllUsers() {
        return userStorage.findAll();
    }

    // Получение пользователя по ID
    public User getUserById(int id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));
    }

    // Добавление друга
    public void addFriend(int userId, int friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Друг не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    // Удаление друга
    public void removeFriend(int userId, int friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Друг не найден"));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    // Получение списка друзей пользователя
    public List<User> getFriends(int userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));
        List<User> friends = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            userStorage.findById(friendId).ifPresent(friends::add);
        }
        return friends;
    }

    // Получение списка общих друзей между двумя пользователями
    public List<User> getMutualFriends(int userId, int otherId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        User other = userStorage.findById(otherId)
                .orElseThrow(() -> new IllegalArgumentException("Друг не найден"));

        Set<Integer> mutualFriends = new HashSet<>(user.getFriends());
        mutualFriends.retainAll(other.getFriends());

        List<User> result = new ArrayList<>();
        for (Integer id : mutualFriends) {
            userStorage.findById(id).ifPresent(result::add);
        }
        return result;
    }
}