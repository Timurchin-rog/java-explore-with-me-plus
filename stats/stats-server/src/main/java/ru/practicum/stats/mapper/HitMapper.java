package ru.practicum.stats.mapper;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HitMapper {
    private static String datePattern = "yyyy-MM-dd HH:mm:ss";

    public static HitDto mapToHitDto(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }

    public static Hit mapFromRequest(NewHitDto newHit) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        return new Hit(
                newHit.getApp(),
                newHit.getUri(),
                newHit.getIp(),
                LocalDateTime.parse(newHit.getTimestamp(), formatter)
        );
    }
}
