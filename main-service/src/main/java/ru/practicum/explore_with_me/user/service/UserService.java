package ru.practicum.explore_with_me.user.service;

import ru.practicum.explore_with_me.user.dto.UserRequestDto;
import ru.practicum.explore_with_me.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserResponseDto createUser(UserRequestDto userRequestDto);

    void deleteUser(Long userId);
}
