package ru.practicum.stats.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.NewHitDto;
import ru.practicum.stats.dto.ViewDto;

import java.util.List;

public interface HitService {

    HitDto saveHit(NewHitDto newHit);

    List<ViewDto> getViews(String start, String end, List<String> uris, boolean isUnique);
}
