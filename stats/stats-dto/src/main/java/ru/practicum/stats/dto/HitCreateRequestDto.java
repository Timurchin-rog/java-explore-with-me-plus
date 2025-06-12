package ru.practicum.stats.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.net.InetAddress;
import java.time.LocalDateTime;

public record HitCreateRequestDto(
        @NotBlank(message = "App must not be blank")
        String app,

        @NotBlank(message = "Uri must not be blank")
        String uri,

        @NotNull(message = "Ip must not be null")
        InetAddress ip,

        @NotNull(message = "Timestamp must not be null")
        LocalDateTime timestamp
) {

}
