package ru.practicum.ewm.compilation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.model.Event;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToMany
    @JoinTable(
            name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    Set<Event> events;

    Boolean pinned;

    String title;

    public Compilation(Set<Event> events, Boolean pinned, String title) {
        this.events = events;
        this.pinned = pinned;
        this.title = title;
    }
}