package ru.practicum.stats.dto;

public record HitResponseDto(
        String app,
        String uri,
        Integer hits
) {

}
