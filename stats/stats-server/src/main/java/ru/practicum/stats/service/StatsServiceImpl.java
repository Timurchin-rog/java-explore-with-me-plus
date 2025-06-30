package ru.practicum.stats.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.NewHitDto;
import ru.practicum.dto.ViewDto;
import ru.practicum.stats.controller.StatsParam;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.mapper.ViewMapper;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.QHit;
import ru.practicum.stats.model.View;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final HitRepository hitRepository;
    private String datePattern = "yyyy-MM-dd HH:mm:ss";

    @Override
    @Transactional
    public HitDto saveHit(NewHitDto newHit) {
        Hit hit = hitRepository.save(HitMapper.mapFromRequest(newHit));
        return HitMapper.mapToHitDto(hit);
    }

    @Override
    public List<ViewDto> getViews(StatsParam param) {
        QHit qHit = QHit.hit;
        List<BooleanExpression> conditions = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        LocalDateTime start = LocalDateTime.parse(param.getStart(), formatter);
        LocalDateTime end = LocalDateTime.parse(param.getEnd(), formatter);

        conditions.add(QHit.hit.timestamp.between(start, end));

        if (param.getUris()!= null) {
            for (String uriParam : param.getUris())
                conditions.add(QHit.hit.uri.eq(uriParam));
        }
        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        Iterable<Hit> hitsFromRep = hitRepository.findAll(finalCondition);
        List<Hit> hits = new ArrayList<>();

        for (Hit hit : hitsFromRep)
            hits.add(hit);

        if (param.isUnique())
            hits = hits.stream()
                    .distinct()
                    .toList();

        List<View> views = hits.stream()
                .map(ViewMapper::mapToView)
                .distinct()
                .peek(view -> {
                    if (param.isUnique())
                        view.setHits(1);
                    else
                        view.setHits(hitRepository.findCountViews(view.getApp(), view.getUri()));
                })
                .sorted(Comparator.comparing(View::getHits).reversed())
                .toList();
        return ViewMapper.mapToViewDto(views);
    }
}
