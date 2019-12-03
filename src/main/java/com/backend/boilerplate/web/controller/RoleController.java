package com.backend.boilerplate.web.controller;

import com.backend.boilerplate.constant.Status;
import com.backend.boilerplate.dto.CreateRoleDto;
import com.backend.boilerplate.dto.Response;
import com.backend.boilerplate.dto.RoleDto;
import com.backend.boilerplate.dto.UpdateRoleDto;
import com.backend.boilerplate.dto.validator.Exist;
import com.backend.boilerplate.dto.validator.ExistRoleValidator;
import com.backend.boilerplate.dto.validator.OwConstraintSequence;
import com.backend.boilerplate.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Bundles all CRUD APIs for Role.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Api(tags = "Role CRUD API")
@RestController
@RequestMapping("/api/v1/role")
@Validated
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "Get a list of Roles with respective permissions", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved the list of roles with respective permissions",
            examples = @Example(value = @ExampleProperty(value = "", mediaType = "application/json"))),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping
    public Response<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value(), roles);
    }

    @ApiOperation(value = "Create an Role with respective permissions", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Role created successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to perform this action on the resource")})
    @PostMapping
    public Response<RoleDto> createRole(
        @ApiParam(value = "role object to store in database table", required = true)
        @RequestBody @Validated(OwConstraintSequence.class) CreateRoleDto createRoleDto) {
        RoleDto roleDto = roleService.createRole(createRoleDto);
        return new Response<>(Status.SUCCESS, HttpStatus.CREATED.value(), roleDto);
    }

    @ApiOperation(value = "Update an Role with respective permissions", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Role with permissions are updated successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to perform this action on the resource"),
        @ApiResponse(code = 404, message = "The resource you were trying to update is not found")})
    @PutMapping
    public Response<RoleDto> updateRole(
        @ApiParam(value = "Update role object", required = true)
        @RequestBody @Validated(OwConstraintSequence.class) UpdateRoleDto updateRoleDto) {
        RoleDto roleDto = roleService.updateRole(updateRoleDto);
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value(), roleDto);
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "Delete an Role", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Role deleted successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to perform this action on the resource"),
        @ApiResponse(code = 404, message = "The resource you were trying to delete is not found")})
    @DeleteMapping("/{uuid}")
    public Response deleteRole(
        @ApiParam(value = "Role UUID from which role object will delete from database table", example = "36ed1611" +
            "-5236-42a3-83ec-d408b45e3c19", required = true) @PathVariable(value = "uuid")
        @Exist(message = "1009", constraintValidator = ExistRoleValidator.class) UUID uuid) {
        roleService.deleteRole(uuid);
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value());
    }

}
