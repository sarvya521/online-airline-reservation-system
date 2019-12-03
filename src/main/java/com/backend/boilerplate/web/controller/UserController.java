package com.backend.boilerplate.web.controller;

import com.backend.boilerplate.constant.Status;
import com.backend.boilerplate.dto.CreateUserDto;
import com.backend.boilerplate.dto.Response;
import com.backend.boilerplate.dto.UpdateUserDto;
import com.backend.boilerplate.dto.UserDto;
import com.backend.boilerplate.dto.UserPageDto;
import com.backend.boilerplate.dto.validator.Exist;
import com.backend.boilerplate.dto.validator.ExistUserValidator;
import com.backend.boilerplate.dto.validator.OwConstraintSequence;
import com.backend.boilerplate.entity.User;
import com.backend.boilerplate.service.PaginationService;
import com.backend.boilerplate.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Bundles all CRUD APIs for User.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Api(tags = "User CRUD API")
@RestController
@RequestMapping("/api/v1/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("UserPaginationService")
    private PaginationService<User, UserPageDto> userPaginationService;

    /**
     * @return Response<UserPageDto>
     * @since 0.0.1
     */
    @ApiOperation(value = "Get a current page of list of Users", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved the list of users", examples = @Example(value =
        @ExampleProperty(value = "", mediaType = "application/json"))),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping
    public Response<UserPageDto> getUsers(
        @RequestParam(required = false, value = "pageNo") Integer pageNo,
        @RequestParam(required = false, value = "pageSize") Integer pageSize,
        @RequestParam(required = false, value = "sortBy") String sortBy,
        @RequestParam(required = false, defaultValue = "true", value = "asc") boolean asc) {
        UserPageDto userPage = userPaginationService.getPageDto(pageNo, pageSize, sortBy, asc);
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value(), userPage);
    }

    @ApiOperation(value = "Get an User by UUID", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved the user details"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping("/{uuid}")
    public Response<UserDto> getUserByUuid(
        @ApiParam(value = "User id from which user object will retrieve", example = "3275ae93-97c1-4181-b96d" +
            "-4d4e1e507a3b", required = true) @PathVariable(value = "uuid") UUID uuid) {
        UserDto user = userService.getUserByUuid(uuid);
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value(), user);
    }

    @ApiOperation(value = "Create an User", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "User created successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to perform this action on the resource")
    })
    @PostMapping
    public Response<UserDto> createUser(
        @ApiParam(value = "user object store in database table", required = true)
        @RequestBody @Validated(OwConstraintSequence.class) CreateUserDto createUserDto) {
        UserDto userDto = userService.createUser(createUserDto);
        return new Response<>(Status.SUCCESS, HttpStatus.CREATED.value(), userDto);
    }

    @ApiOperation(value = "Update an User", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User updated successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to perform this action on the resource"),
        @ApiResponse(code = 404, message = "The resource you were trying to update is not found")})
    @PutMapping
    public Response<UserDto> updateUser(
        @ApiParam(value = "Update user object", required = true)
        @RequestBody @Validated(OwConstraintSequence.class) UpdateUserDto updateUserDto) {
        UserDto userDto = userService.updateUser(updateUserDto);
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value(), userDto);
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "Delete an User", produces = "application/json")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "User deleted successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to perform this action on the resource"),
        @ApiResponse(code = 404, message = "The resource you were trying to delete is not found")})
    @DeleteMapping("/{uuid}")
    public Response deleteUser(
        @ApiParam(value = "User Id from which user object will delete from database table", example = "3275ae93-97c1" +
            "-4181-b96d-4d4e1e507a3b", required = true) @PathVariable(value = "uuid")
        @Exist(message = "1004", constraintValidator = ExistUserValidator.class) UUID uuid) {
        userService.deleteUser(uuid);
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value());
    }
}