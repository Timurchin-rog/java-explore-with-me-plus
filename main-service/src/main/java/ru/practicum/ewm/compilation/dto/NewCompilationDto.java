package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.HashSet;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {
    HashSet<Long> events;
    Boolean pinned;
    @NotBlank(message = "Название подборки не может быть пустым")
    String title;

    public boolean hasEvents() {
        return events != null && !events.isEmpty();
    }

    public boolean hasPinned() {
        return pinned != null;
    }

    public boolean hasTitle() {
        return title != null && !title.isBlank();
    }
}
