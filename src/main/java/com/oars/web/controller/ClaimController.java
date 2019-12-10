package com.oars.web.controller;

import com.oars.constant.Status;
import com.oars.dto.ClaimDto;
import com.oars.dto.Response;
import com.oars.service.ClaimService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Bundles all APIs for Claim.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Api(tags = "Permission API")
@RestController
@RequestMapping("/api/v1/claim")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @ApiOperation(value = "Get a list of Permissions", produces = "application/json")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully retrieved the list of Permissions", examples =
        @Example(value = @ExampleProperty(value = "", mediaType = "application/json"))),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")})
    @GetMapping
    public Response<List<ClaimDto>> getAllClaims() {
        List<ClaimDto> roles = claimService.getAllClaims();
        return new Response<>(Status.SUCCESS, HttpStatus.OK.value(), roles);
    }

}
