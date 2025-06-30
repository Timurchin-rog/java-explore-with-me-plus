package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank(message = "Аннотация не может быть пустой")
    String annotation;
    @NotNull(message = "Событие должно иметь id категории")
    @Positive(message = "Id события не может быть равно 0")
    Long category;
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull(message = "Дата события не может быть пустой")
    String eventDate;
    @NotNull(message = "Объект локации не может быть пустым")
    NewLocationDto location;
    Boolean paid;
    @Positive
    Long participantLimit;
    Boolean requestModeration;
    @NotBlank(message = "Название не может быть пустым")
    String title;
}
