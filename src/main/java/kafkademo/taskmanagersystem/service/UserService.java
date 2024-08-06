package kafkademo.taskmanagersystem.service;

import kafkademo.taskmanagersystem.dto.user.request.UpdateUserRequestDto;
import kafkademo.taskmanagersystem.dto.user.request.UpdateUserRoleDto;
import kafkademo.taskmanagersystem.dto.user.response.ResponseUserDto;

public interface UserService {

    ResponseUserDto updateUserRole(UpdateUserRoleDto updateDto, Long userId);

    ResponseUserDto updateUserProfile(UpdateUserRequestDto updateDto, Long userId);

    ResponseUserDto getUserProfile(Long userId);
}