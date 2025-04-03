-- Создание таблицы пользователей
CREATE TABLE IF NOT EXISTS users (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE NOT NULL
);

-- Создание таблицы рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genres (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Создание таблицы фильмов
CREATE TABLE IF NOT EXISTS films (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INT CHECK (duration > 0),
    mpa_id INT NOT NULL,
    FOREIGN KEY (mpa_id) REFERENCES mpa(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INT,
    friend_id INT,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Таблица связей фильмов и жанров (многие ко многим)
CREATE TABLE IF NOT EXISTS film_genres (
    film_id INT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);