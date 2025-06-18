package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.exception.DuplicatedEmailException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public Page<UserDto> getUsers(List<Long> ids, Pageable page) {
        if (ids == null || ids.isEmpty())
            return UserMapper.fromPage(repository.findAll(page));
        else
            return UserMapper.fromPage(repository.findAllByIdIn(ids, page));
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest userFromRequest) {
        checkDuplicatedEmail(userFromRequest.getEmail());
        User newUser = repository.save(UserMapper.mapFromRequest(userFromRequest));

        return UserMapper.mapToUserDto(newUser);
    }

    @Transactional
    @Override
    public void removeUser(long userId) {
        if (repository.findById(userId).isEmpty()) {
            throw new NotFoundException();
        }
        repository.deleteById(userId);
    }

    private void checkDuplicatedEmail(String email) {
        if (repository.findByEmailLike(email).isPresent())
            throw new DuplicatedEmailException();
    }
}
