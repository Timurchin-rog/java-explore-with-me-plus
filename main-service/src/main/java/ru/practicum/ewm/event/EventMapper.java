package ru.practicum.ewm.event;

import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EventMapper {
    private static String datePattern = "yyyy-MM-dd HH:mm:ss";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);

    public static EventFullDto mapToEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(formatter))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .location(mapToLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().format(formatter))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .build();
    }

    public static List<EventFullDto> mapToEventFullDto(Iterable<Event> events) {
        List<EventFullDto> eventsResult = new ArrayList<>();

        for (Event event : events) {
            eventsResult.add(mapToEventFullDto(event));
        }

        return eventsResult;
    }

    public static EventShortDto mapToEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.mapToCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(formatter))
                .initiator(UserMapper.mapToUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public static HashSet<EventShortDto> mapToEventShortDto (Iterable<Event> events) {
        HashSet<EventShortDto> eventsResult = new HashSet<>();

        for (Event event : events) {
            eventsResult.add(mapToEventShortDto(event));
        }

        return eventsResult;
    }

    public static Location mapFromRequest(NewLocationDto location) {
        return new Location(
                location.getLat(),
                location.getLon()
        );
    }

    public static LocationDto mapToLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Event mapFromRequest(NewEventDto event) {
        return new Event(
                validateAnnotation(event.getAnnotation()),
                validateDescription(event.getDescription()),
                LocalDateTime.parse(event.getEventDate(), formatter),
                mapFromRequest(event.getLocation()),
                validatePaid(event.getPaid()),
                validateParticipantLimit(event.getParticipantLimit()),
                validateRequestModeration(event.getRequestModeration()),
                validateTitle(event.getTitle())
        );
    }

    private static String validateAnnotation(String annotation) {
        if (annotation.length() < 20) {
            throw new ValidationException("Аннотация к событию не может быть меньше 20 символов");
        } else if (annotation.length() > 2000) {
            throw new ValidationException("Аннотация к событию не может быть больше 2000 символов");
        } else {
            return annotation;
        }
    }

    private static String validateDescription(String description) {
        if (description.length() < 20) {
            throw new ValidationException("Описание к событию не может быть меньше 20 символов");
        } else if (description.length() > 7000) {
            throw new ValidationException("Описание к событию не может быть больше 7000 символов");
        } else {
            return description;
        }
    }

    private static Boolean validatePaid(Boolean paid) {
        if (paid == null) {
            paid = false;
        }

        return paid;
    }

    private static Long validateParticipantLimit(Long participantLimit) {
        if (participantLimit == null) {
            participantLimit = 0L;
        }

        return participantLimit;
    }

    private static Boolean validateRequestModeration(Boolean requestModeration) {
        if (requestModeration == null) {
            requestModeration = true;
        }

        return requestModeration;
    }

    private static String validateTitle(String title) {
        if (title.length() < 3) {
            throw new ValidationException("Название события не может быть меньше 3 символов");
        } else if (title.length() > 120) {
            throw new ValidationException("Название события не может быть больше 120 символов");
        } else {
            return title;
        }
    }

    public static Event updateEventFields(Event event, UpdateEventUserRequest eventFromRequest) {
        if (eventFromRequest.hasAnnotation()) {
            event.setAnnotation(validateAnnotation(eventFromRequest.getAnnotation()));
        }

        if (eventFromRequest.hasDescription()) {
            event.setDescription(validateDescription(eventFromRequest.getDescription()));
        }

        if (eventFromRequest.hasEventDate()) {
            event.setEventDate(LocalDateTime.parse(eventFromRequest.getEventDate(), formatter));
        }

        if (eventFromRequest.hasLocation()) {
            event.setLocation(mapFromRequest(eventFromRequest.getLocation()));
        }

        if (eventFromRequest.hasPaid()) {
            event.setPaid(eventFromRequest.getPaid());
        }

        if (eventFromRequest.hasParticipantLimit()) {
            event.setParticipantLimit(eventFromRequest.getParticipantLimit());
        }

        if (eventFromRequest.hasRequestModeration()) {
            event.setRequestModeration(eventFromRequest.getRequestModeration());
        }

        if (eventFromRequest.hasStateAction()) {
            if (eventFromRequest.getStateAction().equalsIgnoreCase("SEND_TO_REVIEW"))
                event.setState(EventState.PUBLISHED);
            else if (eventFromRequest.getStateAction().equalsIgnoreCase("CANCEL_REVIEW"))
                event.setState(EventState.CANCELED);
        }

        if (eventFromRequest.hasTitle()) {
            event.setTitle(validateTitle(eventFromRequest.getTitle()));
        }

        return event;
    }
}