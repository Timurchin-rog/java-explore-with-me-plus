package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.model.View;
import ru.practicum.ewm.repository.HitRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService{

    private final HitRepository hitRepository;

    @Override
    @Transactional
    public HitDto saveHit(NewHitDto newHit) {
        Hit hit = hitRepository.save(newHit);
        return HitMapper.mapToHitDto(hit);
    }

    @Override
    public List<ViewDto> getViews() {
        List<View> viewsWithoutCountHit = hitRepository.findAllDistinctViews();
        List<View> viewsWithCountHit = viewsWithoutCountHit.stream()
                .peek(view -> view.setHits(hitRepository.findCountViews(view.getApp(), view.getUri())))
                .toList();
        return ViewMapper.mapToViewDto(viewsWithCountHit);
    }
}
