package ru.practicum.explore_with_me.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.error.exception.AlreadyExistEwmException;
import ru.practicum.explore_with_me.error.exception.NotFoundEwmException;
import ru.practicum.explore_with_me.user.dto.UserRequestDto;
import ru.practicum.explore_with_me.user.dto.UserResponseDto;
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
        log.info("USER SERVICE: GET users ids: {}, from = {}, size = {}", ids, from, size);

        Pageable pageable = PageRequest.of(from / size, size);

        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        log.info("USER SERVICE: CREATE userRequestDto: {}", userRequestDto);

        userRepository.findByName(userRequestDto.getName())
                .ifPresent(e -> {
                    throw new AlreadyExistEwmException(
                            String.format("A user named %s already exists", userRequestDto.getName()));
                });

        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userRequestDto)));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("USER SERVICE: DELETE user id: {}", userId);

        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundEwmException(String.format("user with id = %d not found", userId)));

        userRepository.deleteById(userId);
    }
}