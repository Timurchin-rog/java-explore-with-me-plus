package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.PrivateEventParam;
import ru.practicum.ewm.event.dto.EventFullDto;

import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsOfUser(PrivateEventParam param);

    EventFullDto getEventOfUser(PrivateEventParam param);

    EventFullDto createEvent(PrivateEventParam param);

    EventFullDto updateEvent(PrivateEventParam param);
}
