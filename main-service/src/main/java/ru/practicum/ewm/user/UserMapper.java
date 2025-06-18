package ru.practicum.ewm.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static Page<UserDto> fromPage(Page<User> users) {
        List<UserDto> usersDto = users.stream()
                .map(UserMapper::mapToUserDto).toList();

        return new PageImpl<>(usersDto, users.getPageable(), users.getTotalElements());
    }

    public static User mapFromRequest(NewUserRequest user) {
        return new User(
                user.getEmail(),
                user.getName()
        );
    }
}
