package com.backend.boilerplate.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"uuid", "resourceName"})
@Data
@NoArgsConstructor
@ApiModel(description = "All details about the claims.")
public class ClaimDto {
    @ApiModelProperty(notes = "The uuid of the claim", position = 0)
    private UUID uuid;

    @ApiModelProperty(notes = "The resource name of the claim", position = 1)
    private String resourceName;

}
