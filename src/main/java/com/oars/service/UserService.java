package com.oars.service;

import com.oars.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long id);

    UserDto checkLogin(String emailId, String password);

    boolean checkIfUserExists(String email);
}
