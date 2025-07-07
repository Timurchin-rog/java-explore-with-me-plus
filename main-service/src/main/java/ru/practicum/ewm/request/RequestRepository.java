package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.request.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
    @Query("SELECT COUNT(r.event) " +
            "FROM Request r " +
            "WHERE r.event = ?1 " +
            "AND r.state = 'STATE'")
    long findCountConfirmedRequests(long eventId);
}
