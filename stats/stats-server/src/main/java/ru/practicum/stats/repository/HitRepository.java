package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {

    @Query("SELECT COUNT(DISTINCT h.app, h.uri) " +
            "FROM Hit h " +
            "WHERE h.app=?1 AND h.uri=?2")
    Integer findCountViews(String app, String uri);

    @Query("SELECT DISTINCT h.app, h.uri " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3")
    List<ViewFromHit> findAllViewsWithoutUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT DISTINCT h.app, h.uri " +
            "FROM Hit h " +
            "WHERE h.timestamp BETWEEN ?1 AND ?2 " +
            "AND h.uri IN ?3 " +
            "AND h.ip LIKE (SELECT DISTINCT h.ip " +
            "FROM Hit h)")
    List<ViewFromHit> findAllViewsWithUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
