package com.oars.service;

import com.oars.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUser(Long id);

    UserDto checkLogin(String emailId, String password);

    boolean checkIfUserExists(String email);

    List<UserDto> getAllCustomers();

    List<UserDto> getAllCustomersWithBooking();

    List<UserDto> getAllAgents();
}
