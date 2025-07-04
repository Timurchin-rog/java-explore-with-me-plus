package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class NewHitDto {
        @NotBlank(message = "App must not be blank")
        String app;

        @NotBlank(message = "Uri must not be blank")
        String uri;

        @NotBlank(message = "Ip must not be null")
        String ip;

        @NotBlank(message = "Timestamp must not be null")
        LocalDateTime timestamp;
}
