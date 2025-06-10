package ru.practicum.ewm.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class View {

    String app;

    String uri;

    Integer hits;
}
