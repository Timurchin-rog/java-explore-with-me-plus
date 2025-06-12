package ru.practicum.stats.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class View {
    String app;
    String uri;
    Integer hits;

    public View(String app, String uri) {
        this.app = app;
        this.uri = uri;
    }
}
