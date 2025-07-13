package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUser_idAndEvent_id(long userId, long eventId, Pageable page);

    List<Comment> findAllByEvent_id(long eventId, Pageable page);
}
