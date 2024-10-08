package kafkademo.taskmanagersystem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Set;
import kafkademo.taskmanagersystem.dto.user.request.RegisterUserRequestDto;
import kafkademo.taskmanagersystem.dto.user.request.UpdateUserRequestDto;
import kafkademo.taskmanagersystem.dto.user.request.UpdateUserRoleDto;
import kafkademo.taskmanagersystem.dto.user.response.ResponseUserDto;
import kafkademo.taskmanagersystem.entity.Role;
import kafkademo.taskmanagersystem.entity.User;
import kafkademo.taskmanagersystem.exception.RegistrationException;
import kafkademo.taskmanagersystem.mapper.UserMapper;
import kafkademo.taskmanagersystem.repo.RoleRepository;
import kafkademo.taskmanagersystem.repo.UserRepository;
import kafkademo.taskmanagersystem.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public ResponseUserDto register(RegisterUserRequestDto requestDto) {
        if (userRepository.findUserByEmail(requestDto.getEmail()).isPresent()) {
            String message = "User with email: "
                    + requestDto.getEmail() + " does already exist";
            log.error(message);
            throw new RegistrationException(message);
        }
        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findAllByRoleName(Role.RoleName.USER)
                .orElseThrow(() -> {
                    String message = "Can not find role by name: " + Role.RoleName.USER;
                    log.error(message);
                    return new EntityNotFoundException(message);
                });
        user.setRoles(Collections.singleton(role));
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    public ResponseUserDto updateUserRole(UpdateUserRoleDto updateDto, Long userId) {
        User user = findUserProfile(userId);

        Role role = roleRepository.findAllByRoleName(Role.RoleName.valueOf(updateDto.getRole()))
                .orElseThrow(() -> {
                    String message = "Can not find role by role name: " + updateDto.getRole();
                    log.error(message);
                    return new EntityNotFoundException(message);
                });
        user.setRoles(Collections.singleton(role));
        log.info("User role updated successfully for userId: {}", userId);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public ResponseUserDto updateUserProfile(UpdateUserRequestDto updateDto, Long userId) {
        User user = findUserProfile(userId);
        user.setNickName(updateDto.getNickName());
        user.setFirstName(updateDto.getUserFirstName());
        user.setLastName(updateDto.getUserLastName());
        log.info("User profile updated successfully for userId: {}", userId);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public ResponseUserDto getUserProfile(Long userId) {
        User user = findUserProfile(userId);
        log.info("User profile retrieved successfully for userId: {}", userId);
        return userMapper.toDto(user);
    }

    @Override
    public Set<Long> getAllUserIds() {
        log.info("Retrieved all user IDs");
        return userRepository.findAllIds();
    }

    @Override
     public User findUserProfile(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            String message = "Can not find user's profile by id: " + userId;
            log.error(message);
            return new EntityNotFoundException(message);
        });
    }

    @Override
    public Set<User> findAllByIdIn(Set<Long> userIds) {
        log.info("Retrieved users by IDs");
        return userRepository.findAllByIdIn(userIds);
    }
}
