package ru.practicum.ewm.event.dto.location;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDto {
    Double lat;
    Double lon;
}
