package ru.practicum.ewm.user;

import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;

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

    public static UserShortDto mapToUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User mapFromRequest(NewUserRequest user) {
        return new User(
                validateEmail(user.getEmail()),
                validateName(user.getName())
        );
    }

    private static String validateEmail(String email) {
        if (email.length() < 6) {
            throw new ValidationException("Имейл не может быть меньше 6 символов");
        } else if (email.length() > 254) {
            throw new ValidationException("Имейл не может быть больше 254 символов");
        } else {
            return email;
        }
    }

    private static String validateName(String name) {
        if (name.length() < 2) {
            throw new ValidationException("Имя не может быть меньше 2 символов");
        } else if (name.length() > 250) {
            throw new ValidationException("Имя не может быть больше 250 символов");
        } else {
            return name;
        }
    }
}
