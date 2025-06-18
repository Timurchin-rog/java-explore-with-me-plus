package ru.practicum.ewm.user;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailLike(String email);

    @NonNull
    Page<User> findAll(@NonNull Pageable page);

    Page<User> findAllByIdIn(List<Long> ids, Pageable page);
}
