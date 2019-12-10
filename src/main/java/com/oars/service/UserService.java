package com.oars.service;

import com.oars.dto.CreateUserDto;
import com.oars.dto.UpdateUserDto;
import com.oars.dto.UserDto;
import com.oars.entity.User;

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
