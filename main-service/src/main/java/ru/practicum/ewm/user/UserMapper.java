package ru.practicum.ewm.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static List<UserDto> mapToUserDto(Iterable<User> users) {
        List<UserDto> usersResult = new ArrayList<>();

        for (User user : users) {
            usersResult.add(mapToUserDto(user));
        }

        return usersResult;
    }

    public static User mapFromRequest(NewUserRequest user) {
        return new User(
                user.getEmail(),
                user.getName()
        );
    }
}
