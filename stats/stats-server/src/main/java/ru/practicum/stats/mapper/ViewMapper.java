package ru.practicum.stats.mapper;

import ru.practicum.stats.dto.ViewDto;
import ru.practicum.stats.model.View;
import ru.practicum.stats.repository.ViewFromHit;

import java.util.ArrayList;
import java.util.List;

public class ViewMapper {
    public static ViewDto mapToViewDto(View view) {
        return ViewDto.builder()
                .app(view.getApp())
                .uri(view.getUri())
                .hits(view.getHits())
                .build();
    }

    public static List<ViewDto> mapToViewDto(Iterable<View> views) {
        List<ViewDto> viewsResult = new ArrayList<>();

        for (View view : views) {
            viewsResult.add(mapToViewDto(view));
        }

        return viewsResult;
    }

    public static View mapToView(ViewFromHit viewFromHit, Integer hits) {
        return new View(
                viewFromHit.getApp(),
                viewFromHit.getUri(),
                hits
        );
    }
}
