package ru.practicum.ewm.service;

public interface HitService {

    HitDto saveHit(NewHitDto newHit);

    List<ViewDto> getViews();
}
