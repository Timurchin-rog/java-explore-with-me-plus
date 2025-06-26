package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.PrivateEventParam;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;

import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsOfUser(PrivateEventParam param);

    EventFullDto createEvent(NewEventDto event, long userId);

}
