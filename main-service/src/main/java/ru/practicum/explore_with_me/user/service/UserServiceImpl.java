package ru.practicum.explore_with_me.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.user.dto.UserRequestDto;
import ru.practicum.explore_with_me.user.dto.UserResponseDto;
import ru.practicum.explore_with_me.user.exception.UserNotFoundException;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserResponseDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.info("SERVICE: GET users ids: {}, from = {}, size = {}", ids, from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        List<UserResponseDto> usersDto;
        if (ids == null || ids.isEmpty()) {
            usersDto = userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            usersDto = userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        log.info("usersDto: {}", usersDto);
        return usersDto;
    }

    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        log.info("SERVICE: CREATE userRequestDto: {}", userRequestDto);

        UserResponseDto userResponseDto = UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userRequestDto)));

        log.info("userResponseDto: {}", userResponseDto);
        return userResponseDto;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("SERVICE: DELETE user id: {}", userId);

        userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("user with id = %d not found", userId)));

        userRepository.deleteById(userId);
    }
}