package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    private int id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза должна быть указана")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;

    private Mpa mpa;

    private Set<Genre> genres = new HashSet<>();

    private Set<Integer> likes = new HashSet<>();

    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private boolean isValidReleaseDate() {
        LocalDate minimumDate = LocalDate.of(1895, 12, 28);
        return releaseDate == null || !releaseDate.isBefore(minimumDate);
    }

    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa, Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = (genres != null) ? genres : new HashSet<>();
    }
}