package com.backend.boilerplate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"uuid", "firstName", "middleName", "lastName", "email", "salutation", "roles", "tenants"})
@Data
@NoArgsConstructor
@ApiModel(description = "All details about the user.")
public class UserDto {

    @ApiModelProperty(notes = "The uuid of the user", position = 0)
    private UUID uuid;

    @ApiModelProperty(notes = "The first name of the user", position = 1)
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user", position = 3)
    private String lastName;

    @ApiModelProperty(notes = "The email id of the user", position = 4)
    private String email;

    @ApiModelProperty(notes = "The role details of the user", position = 6)
    private List<UserRoleDto> roles;
}
