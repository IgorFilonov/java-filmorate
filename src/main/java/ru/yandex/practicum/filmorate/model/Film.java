package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Data
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


    @AssertTrue(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private boolean isValidReleaseDate() {
        LocalDate minimumDate = LocalDate.of(1895, 12, 28);
        return releaseDate == null || !releaseDate.isBefore(minimumDate);
    }
}