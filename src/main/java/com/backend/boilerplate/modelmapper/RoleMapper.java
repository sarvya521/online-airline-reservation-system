package com.backend.boilerplate.modelmapper;

import com.backend.boilerplate.dto.CreateRoleDto;
import com.backend.boilerplate.dto.RoleDto;
import com.backend.boilerplate.dto.UpdateRoleDto;
import com.backend.boilerplate.dto.UserRoleDto;
import com.backend.boilerplate.entity.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Role Mapper to map {@link RoleDto} to {@link Role} and vice-versa
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Component
public class RoleMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Convert {@link Role} to {@link UserRoleDto}
     *
     * @param role
     * @return {@link RoleDto}
     */
    public UserRoleDto convertToUserRoleDto(Role role) {
        return modelMapper.map(role, UserRoleDto.class);
    }

    /**
     * Convert {@link Role} to {@link RoleDto}
     *
     * @param role
     * @return {@link RoleDto}
     */
    public RoleDto convertToDto(Role role) {
        return modelMapper.map(role, RoleDto.class);
    }

    /**
     * Convert {@link RoleDto} to {@link Role}
     *
     * @param roleDto
     * @return {@link Role}
     */
    public Role convertToEntity(RoleDto roleDto) {
        return modelMapper.map(roleDto, Role.class);
    }

    /**
     * Convert {@link CreateRoleDto} to {@link Role}
     *
     * @param createRoleDto
     * @return {@link Role}
     */
    public Role convertToEntity(CreateRoleDto createRoleDto) {
        return modelMapper.map(createRoleDto, Role.class);
    }

    /**
     * Convert List of {@link Role} to List of {@link UserRoleDto}
     *
     * @param roles
     * @return
     */
    public List<UserRoleDto> convertToUserRoleDtos(List<Role> roles) {
        return roles.stream().map(role -> convertToUserRoleDto(role)).collect(Collectors.toList());
    }

    /**
     * Convert List of {@link Role} to List of {@link RoleDto}
     *
     * @param roles
     * @return
     */
    public List<RoleDto> convertToDtos(List<Role> roles) {
        return roles.stream().map(role -> convertToDto(role)).collect(Collectors.toList());
    }

    /**
     * Merge {@link RoleDto} with {@link Role}
     *
     * @param roleDto
     * @param role
     */
    public void mergeToEntity(RoleDto roleDto, Role role) {
        modelMapper.map(roleDto, role);
    }

    /**
     * Merge {@link UpdateRoleDto} with {@link Role}
     *
     * @param updateRoleDto
     * @param role
     */
    public void mergeToEntity(UpdateRoleDto updateRoleDto, Role role) {
        modelMapper.map(updateRoleDto, role);
    }

}
