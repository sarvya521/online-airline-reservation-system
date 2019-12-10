package com.oars.service;

import com.oars.dto.CreateRoleDto;
import com.oars.dto.RoleDto;
import com.oars.dto.UpdateRoleDto;

import java.util.List;
import java.util.UUID;

/**
 * Bundles all CRUD APIs for Role.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public interface RoleService {

    List<RoleDto> getAllRoles();

    RoleDto createRole(CreateRoleDto roleDto);

    RoleDto updateRole(UpdateRoleDto roleDto);

    void deleteRole(UUID uuid);
}
