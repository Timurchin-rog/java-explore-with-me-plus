package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.model.View;

import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {

    @Query("SELECT COUNT(DISTINCT h.app, h.uri) FROM Hit h" +
            "WHERE h.app=?1 AND h.uri=?2")
    Integer findCountViews(String app, String uri);

    @Query("SELECT DISTINCT h.app, h.uri FROM HIT h")
    List<View> findAllDistinctViews();
}
