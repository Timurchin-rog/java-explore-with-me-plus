package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.user.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

@Entity
@Table(name = "compilations")
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToMany
    HashSet<Event> events;

    Boolean pinned;

    String title;

    public Compilation(Boolean pinned, String title) {
        this.pinned = pinned;
        this.title = title;
    }
}