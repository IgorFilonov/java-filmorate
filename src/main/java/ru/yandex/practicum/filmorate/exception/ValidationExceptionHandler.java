package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ValidationExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ValidationExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Ошибки валидации: {}", errors);
        Map<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        response.put("error", "Ошибка валидации");
        response.put("details", errors.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        String message = ex.getMessage() != null ? ex.getMessage() : "Ресурс не найден";
        error.put("status", String.valueOf(HttpStatus.NOT_FOUND.value()));
        error.put("error", message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("status", String.valueOf(ex.getStatusCode().value()));
        error.put("error", ex.getReason());
        log.error("ResponseStatusException: {}", ex.getReason(), ex);
        return ResponseEntity.status(ex.getStatusCode()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        log.error("Неизвестная ошибка: {}", ex.getMessage(), ex);
        Map<String, String> error = new HashMap<>();
        String message = ex.getMessage() != null ? ex.getMessage() : "Внутренняя ошибка сервера";
        error.put("status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        error.put("error", message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

