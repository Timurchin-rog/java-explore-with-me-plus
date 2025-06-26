package ru.practicum.ewm.event;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivateEventParam {
    Long id;
    int from;
    int size;
}
