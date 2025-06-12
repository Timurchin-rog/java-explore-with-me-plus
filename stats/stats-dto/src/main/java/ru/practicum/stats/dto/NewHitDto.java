package ru.practicum.stats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.net.InetAddress;
import java.time.LocalDateTime;

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
