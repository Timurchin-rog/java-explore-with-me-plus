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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto saveHit(NewHitDto newHit) {
        Hit hit = hitRepository.save(HitMapper.mapFromRequest(newHit));
        return HitMapper.mapToHitDto(hit);
    }

    @Override
    public List<ViewDto> getViews(String startStr, String endStr, List<String> uris, boolean isUnique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startStr, formatter);
        LocalDateTime end = LocalDateTime.parse(endStr, formatter);
        List<Hit> hits;
        if (uris == null) {
            hits = hitRepository.findAllHitsWithoutUris(start, end);
            if (isUnique)
                hits = hits.stream()
                        .distinct()
                        .toList();
        } else {
            hits = hitRepository.findAllHitsWithUris(start, end, uris);
            if (isUnique)
                hits = hits.stream()
                        .distinct()
                        .toList();
        }
        List<View> views = hits.stream()
                .map(ViewMapper::mapToView)
                .distinct()
                .peek(view -> {
                    if (isUnique)
                        view.setHits(1);
                    else
                        view.setHits(hitRepository.findCountViews(view.getApp(), view.getUri()));
                })
                .sorted(Comparator.comparing(View::getHits).reversed())
                .toList();
        return ViewMapper.mapToViewDto(views);
    }
}
