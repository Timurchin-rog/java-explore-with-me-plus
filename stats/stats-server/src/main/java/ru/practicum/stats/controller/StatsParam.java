package ru.practicum.stats.controller;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsParam {
    String start;
    String end;
    List<String> uris;
    boolean unique;
}
