package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewHitDto {
        @NotBlank(message = "App must not be blank")
        String app;

        @NotBlank(message = "Uri must not be blank")
        String uri;

        @NotBlank(message = "Ip must not be null")
        String ip;

        @NotBlank(message = "Timestamp must not be null")
        String timestamp;
}
