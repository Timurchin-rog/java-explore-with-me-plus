package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    Page<UserDto> getUsers(List<Long> ids, Pageable page);

    UserDto createUser(NewUserRequest user);

    void removeUser(long userId);
}
