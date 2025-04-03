package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    public List<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        return mpaDbStorage.findMpaById(id)
                .orElseThrow(() -> new IllegalArgumentException("MPA рейтинг не найден"));
    }
}