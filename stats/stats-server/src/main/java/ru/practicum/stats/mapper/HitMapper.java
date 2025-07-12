package ru.practicum.stats.mapper;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.stats.model.Hit;

public class HitMapper {

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

        return new Hit(
                newHit.getApp(),
                newHit.getUri(),
                newHit.getIp(),
                newHit.getTimestamp()
        );
    }
}
