package com.oars.modelmapper;

import com.oars.dto.UserDto;
import com.oars.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
}
