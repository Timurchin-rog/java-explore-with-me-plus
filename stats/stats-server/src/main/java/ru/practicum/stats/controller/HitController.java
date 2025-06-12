package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.NewHitDto;
import ru.practicum.stats.dto.ViewDto;
import ru.practicum.stats.service.HitService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HitController {
    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@RequestBody NewHitDto newHit) {
        return hitService.saveHit(newHit);
    }

    @GetMapping("/stats")
    public List<ViewDto> getViews(@RequestParam String start,
                                  @RequestParam String end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") boolean unique) {
        return hitService.getViews(start, end, uris, unique);
    }
}
