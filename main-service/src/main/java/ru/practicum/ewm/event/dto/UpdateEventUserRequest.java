package ru.practicum.ewm.event.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventUserRequest {
    String annotation;
    Long category;
    String description;
    String eventDate;
    NewLocationDto location;
    Boolean paid;
    Long participantLimit;
    Boolean requestModeration;
    String stateAction;
    String title;

    public boolean hasAnnotation() {
        return annotation != null && !annotation.isBlank();
    }

    public boolean hasCategory() {
        return category != null && category != 0L;
    }

    public boolean hasDescription() {
        return description != null && !description.isBlank();
    }

    public boolean hasEventDate() {
        return eventDate != null;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public boolean hasPaid() {
        return paid != null;
    }

    public boolean hasParticipantLimit() {
        return participantLimit != null;
    }

    public boolean hasRequestModeration() {
        return requestModeration != null;
    }

    public boolean hasStateAction() {
        return stateAction != null && !stateAction.isBlank();
    }

    public boolean hasTitle() {
        return title != null && !title.isBlank();
    }
}
