package ru.yandex.practicum.filmorate.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genreDbStorage.getGenreById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Жанр не найден"));
    }

    public void validateGenresExist(Set<Genre> genres) {
        for (Genre genre : genres) {
            if (genreDbStorage.getGenreById(genre.getId()).isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Жанр с ID " + genre.getId() + " не найден");
            }
        }
    }
}
