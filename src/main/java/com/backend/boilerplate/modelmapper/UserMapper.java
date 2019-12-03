package com.backend.boilerplate.modelmapper;

import com.backend.boilerplate.dto.CreateUserDto;
import com.backend.boilerplate.dto.UpdateUserDto;
import com.backend.boilerplate.dto.UserDto;
import com.backend.boilerplate.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Convert {@link User} entity to {@link UserDto }
     *
     * @param user
     * @return {@link UserDto }
     */
    public UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Convert {@link UserDto} entity to {@link User }
     *
     * @param userDto
     * @return {@link User }
     */
    public User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    /**
     * Merge {@link UserDto} entity to {@link User }
     *
     * @param userDto
     * @return {@link User }
     */
    public void mergeToEntity(UserDto userDto, User user) {
        modelMapper.map(userDto, user);
    }

    /**
     * Convert {@link CreateUserDto} entity to {@link UserDto }
     *
     * @param createUserDto
     * @return {@link UserDto }
     */
    public UserDto convertToDto(CreateUserDto createUserDto) {
        return modelMapper.map(createUserDto, UserDto.class);
    }

    /**
     * Convert {@link UpdateUserDto} entity to {@link UserDto }
     *
     * @param updateUserDto
     * @return {@link UserDto }
     */
    public UserDto convertToDto(UpdateUserDto updateUserDto) {
        return modelMapper.map(updateUserDto, UserDto.class);
    }

}
