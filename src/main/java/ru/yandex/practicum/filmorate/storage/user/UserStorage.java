package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User add(User user); // Добавление пользователя

    User update(User user); // Обновление пользователя

    void delete(int id); // Удаление пользователя

    Optional<User> findById(int id); // Поиск пользователя по ID

    List<User> findAll(); // Получение всех пользователей

    // 👇 Методы для работы с друзьями
    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int otherUserId);
}