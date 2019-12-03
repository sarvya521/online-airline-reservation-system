package com.backend.boilerplate.dto;

import com.backend.boilerplate.dto.validator.Extended;
import com.backend.boilerplate.dto.validator.UniqueResource;
import com.backend.boilerplate.dto.validator.UniqueUserValidator;
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
@JsonPropertyOrder({"uuid", "firstName", "lastName", "email", "roles"})
@Data
@NoArgsConstructor
@ApiModel(description = "All details to update the user.")
@UniqueResource(constraintValidator = UniqueUserValidator.class, groups = Extended.class)
public class UpdateUserDto {

    @ApiModelProperty(notes = "The uuid of the user", position = 0, required = true)
    private UUID uuid;

    @ApiModelProperty(notes = "The first name of the user", position = 1, required = true)
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user", position = 3, required = true)
    private String lastName;

    @ApiModelProperty(notes = "The email id of the user", position = 4, required = true)
    private String email;

    @ApiModelProperty(notes = "The role details of the user", position = 6)
    private List<UUID> roles;
}
