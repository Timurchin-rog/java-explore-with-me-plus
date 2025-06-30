package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.model.Hit;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer>, QuerydslPredicateExecutor<Hit> {

    @Query("SELECT COUNT(h.uri) " +
            "FROM Hit h " +
            "WHERE h.app=?1 AND h.uri=?2")
    Integer findCountViews(String app, String uri);
}
