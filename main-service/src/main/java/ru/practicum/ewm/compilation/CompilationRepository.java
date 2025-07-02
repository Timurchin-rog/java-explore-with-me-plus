package ru.practicum.ewm.compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

public interface CompilationRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
}
