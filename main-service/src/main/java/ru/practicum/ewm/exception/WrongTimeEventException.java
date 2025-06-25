package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class WrongTimeEventException extends RuntimeException {
    public WrongTimeEventException(String message) {
        super(message);
    }
}
