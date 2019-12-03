package com.backend.boilerplate.service;

import com.backend.boilerplate.dto.CreateRoleDto;
import com.backend.boilerplate.dto.RoleDto;
import com.backend.boilerplate.dto.UpdateRoleDto;

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
