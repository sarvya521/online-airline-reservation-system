package com.backend.boilerplate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@JsonInclude(content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"totalRecords", "users"})
@Data
@NoArgsConstructor
@ApiModel(description = "Paginated result of user list")
public class UserPageDto {

    @ApiModelProperty(notes = "The total number available users", position = 0)
    private Long totalRecords = 0l;

    @ApiModelProperty(notes = "List of user with user details", position = 6)
    private List<UserDto> users = new ArrayList<>();

}
