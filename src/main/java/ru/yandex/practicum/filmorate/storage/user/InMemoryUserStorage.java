package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Set<Integer>> friendships = new HashMap<>();
    private int currentId = 1;

    @Override
    public User add(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        friendships.put(user.getId(), new HashSet<>());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(int id) {
        users.remove(id);
        friendships.remove(id);
        // удалим также этого пользователя из списков друзей других пользователей
        for (Set<Integer> friends : friendships.values()) {
            friends.remove(id);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        friendships.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        Set<Integer> friends = friendships.get(userId);
        if (friends != null) {
            friends.remove(friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        Set<Integer> friendIds = friendships.getOrDefault(userId, Collections.emptySet());
        return friendIds.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        Set<Integer> friends1 = friendships.getOrDefault(userId, Collections.emptySet());
        Set<Integer> friends2 = friendships.getOrDefault(otherUserId, Collections.emptySet());

        return friends1.stream()
                .filter(friends2::contains)
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}