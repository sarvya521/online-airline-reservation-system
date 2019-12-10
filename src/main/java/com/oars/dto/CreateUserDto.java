package com.oars.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oars.dto.validator.Extended;
import com.oars.dto.validator.UniqueField;
import com.oars.dto.validator.UniqueUserEmailValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;


@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"firstName", "lastName", "email", "roles"})
@Data
@NoArgsConstructor
@ApiModel(description = "All details to create the user.")
public class CreateUserDto {

    @ApiModelProperty(notes = "The first name of the user", position = 0, required = true)
    private String firstName;

    @ApiModelProperty(notes = "The last name of the user", position = 2, required = true)
    private String lastName;

    @ApiModelProperty(notes = "The email id of the user", position = 3, required = true)
    @UniqueField(message = "1061", constraintValidator = UniqueUserEmailValidator.class, groups = Extended.class)
    private String email;

    @ApiModelProperty(notes = "The role details of the user", position = 5)
    private List<UUID> roles;
}