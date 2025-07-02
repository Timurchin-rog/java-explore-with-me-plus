package ru.practicum.ewm.compilation.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.HashSet;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    HashSet<EventShortDto> events;
    Boolean pinned;
    String title;
}
