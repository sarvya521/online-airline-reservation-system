package com.backend.boilerplate.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

/**
 * Wrapper of error details that has to be sent in Api Response
 *
 * @author sarvesh
 * @version 0.0.1
 * @see com.backend.boilerplate.dto.Response
 * @since 0.0.1
 */
@JsonInclude(value = Include.NON_EMPTY, content = Include.NON_NULL)
@JsonPropertyOrder({"code", "message", "target"})
@Getter
@Setter
@NoArgsConstructor
public class ErrorDetails {
    /**
     * Service Error Code
     */
    @ApiModelProperty(notes = "Service Error Code", position = 0)
    private String code;
    /**
     * Service Error Message
     */
    @ApiModelProperty(notes = "Service Error Message", position = 1)
    private String message;
    /**
     * Error's whereabouts
     */
    @ApiModelProperty(notes = "Error's whereabouts", position = 2)
    private String target;

    /**
     * @param code
     * @param message
     */
    public ErrorDetails(@NonNull String code, @NonNull String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @param code
     * @param message
     * @param target
     */
    public ErrorDetails(@NonNull String code, @NonNull String message, @NonNull String target) {
        this.code = code;
        this.message = message;
        this.target = target;
    }

    @Override
    public String toString() {
        String error = String.format("%s-%s", code, message);
        //if(null != target && !target.isBlank()) { // java 11 >
        if (null != target && target.trim().length() > 0) {
            error = String.format("%s target:%s", error, target);
        }
        return error;
    }
}
