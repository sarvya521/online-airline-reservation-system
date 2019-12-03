package com.backend.boilerplate.web.controller;

import com.backend.boilerplate.dto.ClaimDto;
import com.backend.boilerplate.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public class ClaimControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ClaimController claimController;

    @Mock
    private ClaimService claimService;

    /**
     * Setup for before each test case
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(claimController).build();
    }

    @Test
    public void getAllRoles_ShouldPass_WithData() throws Exception {
        /*********** Setup ************/
        List<ClaimDto> claims = prepareClaimsList();
        Mockito.when(claimService.getAllClaims()).thenReturn(claims);

        /*********** Execute ************/
        mockMvc.perform(get("/api/v1/claim")
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].uuid", is(claims.get(0).getUuid().toString())))
            .andExpect(jsonPath("$.data[0].resourceName", is(claims.get(0).getResourceName())))
            .andExpect(jsonPath("$.data[1].uuid", is(claims.get(1).getUuid().toString())))
            .andExpect(jsonPath("$.data[1].resourceName", is(claims.get(1).getResourceName())))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(claimService, times(1)).getAllClaims();
        verifyNoMoreInteractions(claimService);
    }

    @Test
    public void getAllRoles_ShouldPass_NoData() throws Exception {
        /*********** Setup ************/
        Mockito.when(claimService.getAllClaims()).thenReturn(new ArrayList<>());

        /*********** Execute ************/
        mockMvc.perform(get("/api/v1/claim")
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data", hasSize(0)))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(claimService, times(1)).getAllClaims();
        verifyNoMoreInteractions(claimService);
    }

    private List<ClaimDto> prepareClaimsList() {
        List<ClaimDto> claims = new ArrayList<>();
        claims.add(prepareClaim(UUID.randomUUID(), "UserGetAll"));
        claims.add(prepareClaim(UUID.randomUUID(), "RoleGetAll"));
        return claims;
    }

    private ClaimDto prepareClaim(UUID claimId, String claimName) {
        ClaimDto claim = new ClaimDto();
        claim.setUuid(claimId);
        claim.setResourceName(claimName);
        return claim;
    }
}
