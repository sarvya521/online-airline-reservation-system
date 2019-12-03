package com.backend.boilerplate.web.controller;

import com.backend.boilerplate.TestLocalValidatorFactoryBean;
import com.backend.boilerplate.config.ErrorMessageSourceConfig;
import com.backend.boilerplate.dao.ClaimRepository;
import com.backend.boilerplate.dao.RoleRepository;
import com.backend.boilerplate.dto.ClaimDto;
import com.backend.boilerplate.dto.CreateRoleDto;
import com.backend.boilerplate.dto.RoleDto;
import com.backend.boilerplate.dto.UpdateRoleDto;
import com.backend.boilerplate.service.RoleService;
import com.backend.boilerplate.util.ErrorGeneratorInitializer;
import com.backend.boilerplate.web.exception.UserManagementExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@Import({ErrorMessageSourceConfig.class, ErrorGeneratorInitializer.class, UserManagementExceptionHandler.class})
@ContextConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class RoleControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private RoleController roleController;

    @MockBean
    private RoleService roleService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ClaimRepository claimRepository;

    @Autowired
    private UserManagementExceptionHandler owUserManagementExceptionHandler;

    @Autowired
    private MockServletContext servletContext;

    /**
     * Setup for before each test case
     */
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        Map<String, JpaRepository> repositories = new HashMap<>();
        repositories.put("roleRepository", this.roleRepository);
        repositories.put("claimRepository", this.claimRepository);
        LocalValidatorFactoryBean validatorFactoryBean = new TestLocalValidatorFactoryBean(servletContext,
            repositories);

        mockMvc = standaloneSetup(roleController)
            .setValidator(validatorFactoryBean)
            .setControllerAdvice(owUserManagementExceptionHandler)
            .build();
    }

    @Test
    public void getAllRoles_ShouldPass_RoleWithPermissions() throws Exception {
        /*********** Setup ************/
        List<RoleDto> roles = prepareRolesList();
        Mockito.when(roleService.getAllRoles()).thenReturn(roles);

        /*********** Execute ************/
        mockMvc.perform(get("/api/v1/role")
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data", hasSize(2)))
            .andExpect(jsonPath("$.data[0].uuid", is(roles.get(0).getUuid().toString())))
            .andExpect(jsonPath("$.data[0].name", is(roles.get(0).getName())))
            .andExpect(jsonPath("$.data[0].claims", hasSize(2)))
            .andExpect(jsonPath("$.data[1].uuid", is(roles.get(1).getUuid().toString())))
            .andExpect(jsonPath("$.data[1].name", is(roles.get(1).getName())))
            .andExpect(jsonPath("$.data[1].claims", hasSize(2)))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(roleService, times(1)).getAllRoles();
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void getAllRoles_ShouldPass_NoData() throws Exception {
        /*********** Setup ************/
        Mockito.when(roleService.getAllRoles()).thenReturn(new ArrayList<>());

        /*********** Execute ************/
        mockMvc.perform(get("/api/v1/role")
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data", hasSize(0)))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(roleService, times(1)).getAllRoles();
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void createRole_ShouldPass_ValidData() throws Exception {
        /*********** Setup ************/
        CreateRoleDto createRoleDto = prepareCreateRoleDto("Team Lead");
        RoleDto roleDto = prepareRoleDto("Team Lead");
        Mockito.when(roleService.createRole(createRoleDto)).thenReturn(roleDto);
        Mockito.when(roleRepository.countByNameIgnoreCase(createRoleDto.getName())).thenReturn(Optional.of(0L));
        Mockito.when(claimRepository.countByUuidIn(createRoleDto.getClaims())).thenReturn(Optional.of((long) createRoleDto.getClaims().size()));

        /*********** Execute ************/
        mockMvc.perform(post("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(createRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(201)))
            .andExpect(jsonPath("$.data.uuid", is(roleDto.getUuid().toString())))
            .andExpect(jsonPath("$.data.name", is(roleDto.getName())))
            .andExpect(jsonPath("$.data.claims", hasSize(2)))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(roleService, times(1)).createRole(createRoleDto);
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void createRole_ShouldFail_NoRoleName() throws Exception {
        /*********** Setup ************/
        CreateRoleDto createRoleDto = prepareCreateRoleDto("");

        /*********** Execute ************/
        mockMvc.perform(post("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(createRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is("CLIENT_ERROR")))
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[*].code", containsInAnyOrder("1010")))
            .andExpect(jsonPath("$.errors[*].message", containsInAnyOrder("Please provide role name")));

        /*********** Verify/Assertions ************/
        verify(roleService, never()).createRole(createRoleDto);
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void createRole_ShouldFail_IfNoClaims() throws Exception {
        /*********** Setup ************/
        CreateRoleDto createRoleDto = prepareCreateRoleDtoWithNoClaims("Team Lead");
        Mockito.when(roleRepository.countByNameIgnoreCase(createRoleDto.getName())).thenReturn(Optional.of(0L));
        Mockito.when(claimRepository.countByUuidIn(createRoleDto.getClaims())).thenReturn(Optional.of((long) createRoleDto.getClaims().size()));


        /*********** Execute ************/
        mockMvc.perform(post("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(createRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is("CLIENT_ERROR")))
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].code", is("1020")))
            .andExpect(jsonPath("$.errors[0].message", is("Minimum one claim required to create or update role")));

        /*********** Verify/Assertions ************/
        verify(roleService, never()).createRole(createRoleDto);
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void createRole_ShouldFail_NameIsGreaterThan50() throws Exception {
        /*********** Setup ************/
        CreateRoleDto createRoleDto = prepareCreateRoleDto(
            "Checking the role name with long text more than fifty characters it should fail");

        /*********** Execute ************/
        mockMvc.perform(post("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(createRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is("CLIENT_ERROR")))
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].code", is("1011")))
            .andExpect(jsonPath("$.errors[0].message", is("Role name length must be less than or equal to 50")));

        /*********** Verify/Assertions ************/
        verify(roleService, never()).createRole(createRoleDto);
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void updateRole_ShouldPass_ValidData() throws Exception {
        /*********** Setup ************/
        RoleDto roleDto = prepareRoleDto("Team Lead");
        String newRoleName = "Project Lead";
        UpdateRoleDto updateRoleDto = prepareUpdateRoleDto(newRoleName, roleDto);
        roleDto.setName(newRoleName);
        Mockito.when(roleRepository.countByUuid(updateRoleDto.getUuid()))
            .thenReturn(Optional.of(1L));
        Mockito.when(roleRepository.countByUuidNotAndNameIgnoreCase(updateRoleDto.getUuid(), updateRoleDto.getName()))
            .thenReturn(Optional.of(0L));
        Mockito.when(claimRepository.countByUuidIn(updateRoleDto.getClaims()))
            .thenReturn(Optional.of((long)updateRoleDto.getClaims().size()));
        Mockito.when(roleService.updateRole(updateRoleDto)).thenReturn(roleDto);

        /*********** Execute ************/
        mockMvc.perform(put("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(updateRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data.uuid", is(updateRoleDto.getUuid().toString())))
            .andExpect(jsonPath("$.data.name", is(updateRoleDto.getName())))
            .andExpect(jsonPath("$.data.claims", hasSize(2)))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(roleService, times(1)).updateRole(updateRoleDto);
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void updateRole_ShouldFail_NoUUID() throws Exception {
        /*********** Setup ************/
        String roleName = "Team Lead";
        RoleDto roleDto = prepareRoleDto(roleName);
        UpdateRoleDto updateRoleDto = prepareUpdateRoleDto(roleName, roleDto);
        updateRoleDto.setUuid(null);

        /*********** Execute ************/
        mockMvc.perform(put("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(updateRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is("CLIENT_ERROR")))
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].code", is("1013")))
            .andExpect(jsonPath("$.errors[0].message", is("Please provide UUID")));

        /*********** Verify/Assertions ************/
        verify(roleService, never()).updateRole(Mockito.any(UpdateRoleDto.class));
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void updateRole_ShouldFail_NoRoleName() throws Exception {
        /*********** Setup ************/
        String roleName = "Team Lead";
        RoleDto roleDto = prepareRoleDto(roleName);
        UpdateRoleDto updateRoleDto = prepareUpdateRoleDto(roleName, roleDto);
        updateRoleDto.setName(null);

        /*********** Execute ************/
        mockMvc.perform(put("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(updateRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is("CLIENT_ERROR")))
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].code", is("1010")))
            .andExpect(jsonPath("$.errors[0].message", is("Please provide role name")));

        /*********** Verify/Assertions ************/
        verify(roleService, never()).updateRole(Mockito.any(UpdateRoleDto.class));
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void updateRole_ShouldFail_IfNoClaims() throws Exception {
        /*********** Setup ************/
        String roleName = "Team Lead";
        RoleDto roleDto = prepareRoleDto(roleName);
        UpdateRoleDto updateRoleDto = prepareUpdateRoleDto(roleName, roleDto);
        updateRoleDto.setClaims(null);

        /*********** Execute ************/
        mockMvc.perform(put("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(updateRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is("CLIENT_ERROR")))
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].code", is("1020")))
            .andExpect(jsonPath("$.errors[0].message", is("Minimum one claim required to create or update role")));

        /*********** Verify/Assertions ************/
        verify(roleService, never()).updateRole(Mockito.any(UpdateRoleDto.class));
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void updateRole_ShouldFail_NameIsGreaterThan50() throws Exception {
        /*********** Setup ************/
        String roleName = "Checking the role name with long text more than fifty characters it should fail";
        RoleDto roleDto = prepareRoleDto(roleName);
        UpdateRoleDto updateRoleDto = prepareUpdateRoleDto(roleName, roleDto);

        /*********** Execute ************/
        mockMvc.perform(put("/api/v1/role").content(new ObjectMapper().writeValueAsBytes(updateRoleDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status", is("CLIENT_ERROR")))
            .andExpect(jsonPath("$.code", is(400)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(1)))
            .andExpect(jsonPath("$.errors[0].code", is("1011")))
            .andExpect(jsonPath("$.errors[0].message", is("Role name length must be less than or equal to 50")));

        /*********** Verify/Assertions ************/
        verify(roleService, never()).updateRole(Mockito.any(UpdateRoleDto.class));
        verifyNoMoreInteractions(roleService);
    }

    @Test
    public void deleteRole_ShouldPass_ValidData() throws Exception {
        /*********** Setup ************/
        UUID roleId = UUID.randomUUID();
        Mockito.doNothing().when(roleService).deleteRole(roleId);

        /*********** Execute ************/
        mockMvc.perform(delete("/api/v1/role/" + roleId)
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data").doesNotExist())
            .andExpect(jsonPath("$.errors", hasSize(0)));

        /*********** Verify/Assertions ************/
        verify(roleService, times(1)).deleteRole(roleId);
        verifyNoMoreInteractions(roleService);
    }

    private List<RoleDto> prepareRolesList() {
        List<RoleDto> list = new ArrayList<>();
        list.add(prepareRoleDto("Team Lead"));
        list.add(prepareRoleDto("Manager"));
        return list;
    }

    private RoleDto prepareRoleDto(String roleName) {
        RoleDto roleDto = new RoleDto();
        roleDto.setUuid(UUID.randomUUID());
        roleDto.setName(roleName);
        roleDto.setClaims(prepareClaimsList());
        return roleDto;
    }

    private CreateRoleDto prepareCreateRoleDto(String roleName) {
        CreateRoleDto createRoleDto = new CreateRoleDto();
        createRoleDto.setName(roleName);
        createRoleDto.setClaims(prepareClaimUuidList());
        return createRoleDto;
    }

    private CreateRoleDto prepareCreateRoleDtoWithNoClaims(String roleName) {
        CreateRoleDto createRoleDto = new CreateRoleDto();
        createRoleDto.setName(roleName);
        return createRoleDto;
    }

    private UpdateRoleDto prepareUpdateRoleDto(String roleName, RoleDto roleDto) {
        UpdateRoleDto updateRoleDto = new UpdateRoleDto();
        updateRoleDto.setUuid(roleDto.getUuid());
        updateRoleDto.setName(roleName);
        List<UUID> claims = roleDto.getClaims().stream()
            .map(ClaimDto::getUuid).collect(Collectors.toList());
        updateRoleDto.setClaims(claims);
        return updateRoleDto;
    }

    private List<UUID> prepareClaimUuidList() {
        List<UUID> claims = new ArrayList<>();
        claims.add(UUID.randomUUID());
        claims.add(UUID.randomUUID());
        return claims;
    }

    private List<ClaimDto> prepareClaimsList() {
        List<ClaimDto> claims = new ArrayList<>();
        claims.add(prepareClaim("UserGetAll"));
        claims.add(prepareClaim("UserCreate"));
        return claims;
    }

    private ClaimDto prepareClaim(String resourceName) {
        ClaimDto claimDto = new ClaimDto();
        claimDto.setUuid(UUID.randomUUID());
        claimDto.setResourceName(resourceName);
        return claimDto;
    }

}
