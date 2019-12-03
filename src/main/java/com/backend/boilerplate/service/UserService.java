package com.backend.boilerplate.service;

import com.backend.boilerplate.dto.CreateUserDto;
import com.backend.boilerplate.dto.UpdateUserDto;
import com.backend.boilerplate.dto.UserDto;
import com.backend.boilerplate.entity.User;

import java.util.UUID;

/**
 * Bundles all CRUD APIs for User.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public interface UserService {

    UserDto prepareUserDto(User user);

    UserDto getUserByUuid(UUID uuid);

    UserDto createUser(CreateUserDto userDto);

    UserDto updateUser(UpdateUserDto userDto);

    void deleteUser(UUID uuid);
}
