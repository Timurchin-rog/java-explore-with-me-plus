package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.PrivateEventParam;
import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.PrivateEventParam;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventFullDto;

import java.util.List;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.Collection;
import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsOfUser(PrivateEventParam param);

    EventFullDto getEventOfUser(PrivateEventParam param);

    EventFullDto createEvent(PrivateEventParam param);

    EventFullDto updateEvent(PrivateEventParam param);

    List<ParticipationRequestDto> getRequestsOfUser(PrivateEventParam param);

    EventRequestStatusUpdateResult updateStatusOfRequests(PrivateEventParam param);

    Collection<EventShortDto> getPublicAllEvents(EventFilter filter, HttpServletRequest request);

    EventFullDto getPublicEvent(Long eventId, HttpServletRequest request);

    Collection<EventFullDto> getAdminAllEvents(EventFilter filter);

    EventFullDto updateByAdmin(Long eventId, UpdateEventUserRequest updateEvent);
}
