package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;
    private final String eventsPath = "/{user-id}/events";

    @GetMapping(eventsPath)
    public List<EventFullDto> createEvent(@PathVariable(name = "user-id") long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        PrivateEventParam param = PrivateEventParam.builder()
                .id(userId)
                .from(from)
                .size(size)
                .build();
        return eventService.getEventsOfUser(param);
    }

    @PostMapping(eventsPath)
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@Valid @RequestBody NewEventDto event,
                                    @PathVariable(name = "user-id") long userId) {
        return eventService.createEvent(event, userId);
    }
}