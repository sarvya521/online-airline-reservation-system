package com.oars.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.oars.dto.validator.Exist;
import com.oars.dto.validator.ExistClaimsValidator;
import com.oars.dto.validator.Extended;
import com.oars.dto.validator.UniqueField;
import com.oars.dto.validator.UniqueRoleNameValidator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"name", "claims"})
@Data
@NoArgsConstructor
@ApiModel(description = "All details about creating the role.")
public class CreateRoleDto {

    @ApiModelProperty(notes = "The name of the role", position = 1)
    @NotBlank(message = "1010")
    @Size(max = 50, message = "1011")
    @UniqueField(message = "1008", constraintValidator = UniqueRoleNameValidator.class, groups = Extended.class)
    private String name;

    @ApiModelProperty(notes = "List of claims for this role", position = 2)
    @Size(min = 1, message = "1020")
    @Exist(message = "1015", constraintValidator = ExistClaimsValidator.class, groups = Extended.class)
    private List<UUID> claims = new ArrayList<>();
}
