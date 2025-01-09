package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerTests {

    private UserController userController;
    private FilmController filmController;
    private UserService userService;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        filmService = mock(FilmService.class);
        userController = new UserController(userService);
        filmController = new FilmController(filmService);
    }

    // Test for UserController
    @Test
    void addUser_ShouldReturnUser_WhenValidRequest() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@test.com");
        user.setLogin("testUser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        when(userService.addUser(Mockito.any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.addUser(user);
        assertEquals(HttpStatus.CREATED, response.getStatusCode()); // Изменено на CREATED
        assertNotNull(response.getBody());
        assertEquals(user.getId(), response.getBody().getId());
    }

    @Test
    void getUserById_ShouldReturn404_WhenUserNotFound() {
        when(userService.getUserById(1)).thenThrow(new IllegalArgumentException("User not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userController.getUserById(1));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenValidRequest() {
        User user = new User();
        user.setId(1);
        user.setEmail("updated@test.com");
        user.setLogin("updatedUser");
        user.setName("Updated User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        when(userService.updateUser(Mockito.any(User.class))).thenReturn(user);

        ResponseEntity<User> response = userController.updateUser(user);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updated@test.com", response.getBody().getEmail());
    }

    @Test
    void addFriend_ShouldAddFriendSuccessfully() {
        doNothing().when(userService).addFriend(1, 2);

        ResponseEntity<Void> response = userController.addFriend(1, 2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).addFriend(1, 2);
    }

    @Test
    void removeFriend_ShouldRemoveFriendSuccessfully() {
        doNothing().when(userService).removeFriend(1, 2);

        ResponseEntity<Void> response = userController.removeFriend(1, 2);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).removeFriend(1, 2);
    }

    // Test for FilmController
    @Test
    void addFilm_ShouldReturnFilm_WhenValidRequest() {
        Film film = new Film();
        film.setId(1);
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        when(filmService.addFilm(Mockito.any(Film.class))).thenReturn(film);

        Film response = filmController.addFilm(film);
        assertNotNull(response);
        assertEquals("Test Film", response.getName());
    }

    @Test
    void getPopularFilms_ShouldReturnListOfFilms() {
        Film film = new Film();
        film.setId(1);
        film.setName("Popular Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(120);

        when(filmService.getPopularFilms(10)).thenReturn(Collections.singletonList(film));

        List<Film> response = filmController.getPopularFilms(10);
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Popular Film", response.get(0).getName());
    }

    @Test
    void addLike_ShouldAddLikeSuccessfully() {
        doNothing().when(filmService).addLike(1, 1);

        filmController.addLike(1, 1);
        verify(filmService, times(1)).addLike(1, 1);
    }

    @Test
    void removeLike_ShouldRemoveLikeSuccessfully() {
        doNothing().when(filmService).removeLike(1, 1);

        filmController.removeLike(1, 1);
        verify(filmService, times(1)).removeLike(1, 1);
    }
}
