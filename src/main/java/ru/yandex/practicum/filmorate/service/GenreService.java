package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    public List<Genre> getAllGenres() {
        return genreDbStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genreDbStorage.findGenreById(id)
                .orElseThrow(() -> new IllegalArgumentException("Жанр не найден"));
    }
}