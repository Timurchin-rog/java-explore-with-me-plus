package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.NewHitDto;
import ru.practicum.stats.dto.ViewDto;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.mapper.ViewMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.View;
import ru.practicum.stats.repository.HitRepository;
import ru.practicum.stats.repository.ViewFromHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto saveHit(NewHitDto newHit) {
        Hit hit = hitRepository.save(HitMapper.mapFromRequest(newHit));
        return HitMapper.mapToHitDto(hit);
    }

    @Override
    public List<ViewDto> getViews(String startStr, String endStr, List<String> uris, boolean unique) {
        List<ViewFromHit> viewsWithoutCountHit;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);
        if (unique)
            viewsWithoutCountHit = hitRepository.findAllViewsWithUniqueIp(start, end, uris);
        else
            viewsWithoutCountHit = hitRepository.findAllViewsWithoutUniqueIp(start, end, uris);
        List<View> viewsWithCountHit = viewsWithoutCountHit.stream()
                .map(viewFromHit -> ViewMapper.mapToView(viewFromHit,
                        hitRepository.findCountViews(viewFromHit.getApp(), viewFromHit.getUri())))
                .toList();
        return ViewMapper.mapToViewDto(viewsWithCountHit);
    }
}
