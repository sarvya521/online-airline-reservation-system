package com.oars.service;

import com.oars.dto.ClaimDto;

import java.util.List;

/**
 * Bundles all CRUD APIs for Role.
 *
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public interface ClaimService {

    List<ClaimDto> getAllClaims();

}
