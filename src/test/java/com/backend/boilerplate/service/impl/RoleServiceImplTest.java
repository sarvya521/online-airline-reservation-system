package com.backend.boilerplate.service.impl;

import com.backend.boilerplate.config.ModelMapperConfig;
import com.backend.boilerplate.dao.ClaimRepository;
import com.backend.boilerplate.dao.RoleClaimRepository;
import com.backend.boilerplate.dao.RoleHistoryRepository;
import com.backend.boilerplate.dao.RoleRepository;
import com.backend.boilerplate.dao.UserRepository;
import com.backend.boilerplate.dao.UserRoleRepository;
import com.backend.boilerplate.dto.ClaimDto;
import com.backend.boilerplate.dto.CreateRoleDto;
import com.backend.boilerplate.dto.RoleDto;
import com.backend.boilerplate.dto.UpdateRoleDto;
import com.backend.boilerplate.entity.Claim;
import com.backend.boilerplate.entity.Role;
import com.backend.boilerplate.entity.RoleClaim;
import com.backend.boilerplate.entity.RoleHistory;
import com.backend.boilerplate.entity.User;
import com.backend.boilerplate.entity.UserRole;
import com.backend.boilerplate.exception.RoleNotFoundException;
import com.backend.boilerplate.exception.UserManagementException;
import com.backend.boilerplate.modelmapper.ClaimMapper;
import com.backend.boilerplate.modelmapper.RoleMapper;
import com.backend.boilerplate.util.ErrorGeneratorInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author sarvesh
 * @version 0.0.2
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@Import({ClaimMapper.class, RoleMapper.class, ModelMapper.class, ModelMapperConfig.class,
    ErrorGeneratorInitializer.class})
public class RoleServiceImplTest {

    private static final Long PERFORMED_BY = 1L;
    private RoleServiceImpl roleServiceImpl;
    @Mock
    private RoleRepository roleRepositoryMock;
    @Mock
    private ClaimRepository claimRepositoryMock;
    @Mock
    private RoleClaimRepository roleClaimRepositoryMock;
    @Mock
    private RoleHistoryRepository roleHistoryRepositoryMock;
    @Mock
    private UserRoleRepository userRoleRepositoryMock;
    @Mock
    private PlatformTransactionManager platformTransactionManagerMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private ClaimMapper claimMapper;

    /**
     * Setup for before each test case
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        roleServiceImpl = new RoleServiceImpl(platformTransactionManagerMock);

        ReflectionTestUtils.setField(roleServiceImpl, "roleRepository", roleRepositoryMock);
        ReflectionTestUtils.setField(roleServiceImpl, "claimRepository", claimRepositoryMock);
        ReflectionTestUtils.setField(roleServiceImpl, "roleClaimRepository", roleClaimRepositoryMock);
        ReflectionTestUtils.setField(roleServiceImpl, "roleHistoryRepository", roleHistoryRepositoryMock);
        ReflectionTestUtils.setField(roleServiceImpl, "userRoleRepository", userRoleRepositoryMock);
        ReflectionTestUtils.setField(roleServiceImpl, "userRepository", userRepositoryMock);
        ReflectionTestUtils.setField(roleServiceImpl, "roleMapper", roleMapper);
        ReflectionTestUtils.setField(roleServiceImpl, "claimMapper", claimMapper);

        UUID performedByUUID = UUID.randomUUID();
        Mockito.when(userRepositoryMock.findIdByUuid(performedByUUID)).thenReturn(Optional.of(1L));
    }

    @Test
    void getAllRoles_ShouldPass_WithData() {
        /*********** Setup ************/
        List<Role> roleList = prepareRolesList();
        Mockito.when(roleRepositoryMock.findAll()).thenReturn(roleList);

        /*********** Execute ************/
        List<RoleDto> roleDtos = roleServiceImpl.getAllRoles();

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).findAll();
        assertNotNull(roleDtos);
        assertEquals(2, roleDtos.size());
        RoleDto roleDto = roleDtos.get(0);
        Role role = roleList.get(0);
        assertEquals(role.getUuid().toString(), roleDto.getUuid().toString());
        assertEquals(role.getName(), roleDto.getName());
        assertNotNull(role.getRoleClaims());
        assertEquals(role.getRoleClaims().size(), roleDto.getClaims().size());
        assertEquals(role.getRoleClaims().stream().findFirst().get().getClaim().getUuid().toString(),
            roleDto.getClaims().get(0).getUuid().toString());
        assertEquals(role.getRoleClaims().stream().findFirst().get().getClaim().getResourceName(),
            roleDto.getClaims().get(0).getResourceName());
    }

    @Test
    void getAllRoles_ShouldPass_NoData() {
        /*********** Setup ************/
        Mockito.when(roleRepositoryMock.findAll()).thenReturn(new ArrayList<>());

        /*********** Execute ************/
        List<RoleDto> roleDtos = roleServiceImpl.getAllRoles();

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).findAll();
        assertNotNull(roleDtos);
        assertEquals(0, roleDtos.size());
    }

    @Test
    void createRole_ShouldPass() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();
        UUID claimUuid = UUID.randomUUID();
        String roleName = "Manager";

        CreateRoleDto createRoleDto = prepareCreateRoleDto(roleName, claimUuid);
        Mockito.when(roleRepositoryMock.findByNameIgnoreCase(roleName)).thenReturn(Optional.empty());
        Optional<Claim> claimOptional = Optional.of(prepareClaim(1L, claimUuid, "UserGetAll"));
        Mockito.when(claimRepositoryMock.findByUuid(claimUuid)).thenReturn(claimOptional);
        Role roleMock = Mockito.mock(Role.class);
        Mockito.when(roleRepositoryMock.saveAndFlush(Mockito.any(Role.class))).thenReturn(roleMock);

        /*********** Execute ************/
        roleServiceImpl.createRole(createRoleDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(claimRepositoryMock, Mockito.times(1)).findByUuid(claimUuid);
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(RoleHistory.class));
        //Mockito.verify(roleClaimRepositoryMock, Mockito.times(1)).saveAll(ArgumentMatchers.<RoleClaim>anyList());
    }

    @Test
    void updateRole_ShouldPass_WithSameClaims() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();
        UUID claimUuid = UUID.randomUUID();
        String roleName = "Manager";
        UpdateRoleDto roleDto = prepareUpdateRoleDto(roleUuid, roleName, claimUuid);
        Long roleId = 1L;
        Long claimId = 1L;
        Role role = prepareRole(roleId, roleUuid, roleName, claimId, claimUuid);
        Mockito.when(roleRepositoryMock.findByUuid(roleDto.getUuid())).thenReturn(Optional.of(role));
        Optional<Claim> claimOptional = Optional.of(prepareClaim(claimId, claimUuid, "UserGetAll"));
        Mockito.when(claimRepositoryMock.findByUuid(claimUuid)).thenReturn(claimOptional);
        Role roleMock = Mockito.mock(Role.class);
        Mockito.when(roleRepositoryMock.saveAndFlush(Mockito.any(Role.class))).thenReturn(roleMock);

        /*********** Execute ************/
        roleServiceImpl.updateRole(roleDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(claimRepositoryMock, Mockito.times(1)).findByUuid(claimUuid);
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(RoleHistory.class));
        //Mockito.verify(roleClaimRepositoryMock, Mockito.never()).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        //Mockito.verify(roleClaimRepositoryMock, Mockito.never()).saveAll(ArgumentMatchers.<RoleClaim>anyList());
    }

    @Test
    void updateRole_ShouldPass_RemoveAllExistingClaims() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();
        UUID claimUuid = UUID.randomUUID();
        String roleName = "Manager";
        UpdateRoleDto roleDto = prepareUpdateRoleDto(roleUuid, roleName, claimUuid);
        roleDto.setClaims(new ArrayList<>());
        Long roleId = 1L;
        Long claimId = 1L;
        Role role = prepareRole(roleId, roleUuid, roleName, claimId, UUID.randomUUID());
        Mockito.when(roleRepositoryMock.findByUuid(roleDto.getUuid())).thenReturn(Optional.of(role));
        Role roleMock = Mockito.mock(Role.class);
        Mockito.when(roleRepositoryMock.saveAndFlush(Mockito.any(Role.class))).thenReturn(roleMock);

        /*********** Execute ************/
        roleServiceImpl.updateRole(roleDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(RoleHistory.class));
        //Mockito.verify(roleClaimRepositoryMock, Mockito.times(1)).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        //Mockito.verify(roleClaimRepositoryMock, Mockito.never()).saveAll(ArgumentMatchers.<RoleClaim>anyList());
    }

    @Test
    void updateRole_ShouldPass_RemoveOneExistingClaim() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();
        UUID claimUuid = UUID.randomUUID();
        String roleName = "Manager";
        UpdateRoleDto roleDto = prepareUpdateRoleDto(roleUuid, roleName, claimUuid);

        Long roleId = 1L;
        Long claimId = 1L;
        Role role = prepareRole(roleId, roleUuid, roleName, claimId, UUID.randomUUID());
        Long newClaimId = 2L;
        Claim claim = prepareClaim(newClaimId, UUID.randomUUID(), "UserCreate");
        role.getRoleClaims().add(new RoleClaim(role, claim, PERFORMED_BY));

        Optional<Claim> claimOptional = Optional.of(prepareClaim(claimId, claimUuid, "UserGetAll"));
        Mockito.when(claimRepositoryMock.findByUuid(claimUuid)).thenReturn(claimOptional);

        Mockito.when(roleRepositoryMock.findByUuid(roleDto.getUuid())).thenReturn(Optional.of(role));
        Role roleMock = Mockito.mock(Role.class);
        Mockito.when(roleRepositoryMock.saveAndFlush(Mockito.any(Role.class))).thenReturn(roleMock);

        /*********** Execute ************/
        roleServiceImpl.updateRole(roleDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(RoleHistory.class));
        //Mockito.verify(roleClaimRepositoryMock, Mockito.times(1)).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        //Mockito.verify(roleClaimRepositoryMock, Mockito.never()).saveAll(ArgumentMatchers.<RoleClaim>anyList());
    }

    @Test
    void updateRole_ShouldPass_AddNewClaim() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();
        UUID claimUuid = UUID.randomUUID();
        String roleName = "Manager";
        UpdateRoleDto roleDto = prepareUpdateRoleDto(roleUuid, roleName, claimUuid);

        ClaimDto newClaim = new ClaimDto();
        newClaim.setUuid(UUID.randomUUID());
        newClaim.setResourceName("UserCreate");
        roleDto.getClaims().add(newClaim.getUuid());

        Long roleId = 1L;
        Long claimId = 1L;
        Optional<Claim> claimOptional = Optional.of(prepareClaim(claimId, claimUuid, "UserGetAll"));
        Mockito.when(claimRepositoryMock.findByUuid(claimUuid)).thenReturn(claimOptional);
        Long newClaimId = 2L;
        Optional<Claim> newClaimOptional = Optional.of(prepareClaim(newClaimId, newClaim.getUuid(),
            newClaim.getResourceName()));
        Mockito.when(claimRepositoryMock.findByUuid(newClaim.getUuid())).thenReturn(newClaimOptional);

        Role role = prepareRole(roleId, roleUuid, roleName, claimId, claimUuid);
        Mockito.when(roleRepositoryMock.findByUuid(roleDto.getUuid())).thenReturn(Optional.of(role));
        Role roleMock = Mockito.mock(Role.class);
        Mockito.when(roleRepositoryMock.saveAndFlush(Mockito.any(Role.class))).thenReturn(roleMock);

        /*********** Execute ************/
        roleServiceImpl.updateRole(roleDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(RoleHistory.class));
        //Mockito.verify(roleClaimRepositoryMock, Mockito.never()).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        //Mockito.verify(roleClaimRepositoryMock, Mockito.times(1)).saveAll(ArgumentMatchers.<RoleClaim>anyList());
    }

    @Test
    void updateRole_ShouldPass_RemoveExistingAddNewClaim() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();
        UUID claimUuid = UUID.randomUUID();
        String roleName = "Manager";
        UpdateRoleDto roleDto = prepareUpdateRoleDto(roleUuid, roleName, claimUuid);

        Long roleId = 1L;
        Long newClaimId = 1L;
        Optional<Claim> claimOptional = Optional.of(prepareClaim(newClaimId, claimUuid, "UserGetAll"));
        Mockito.when(claimRepositoryMock.findByUuid(claimUuid)).thenReturn(claimOptional);

        Long existingClaimId = 2L;
        Role role = prepareRole(roleId, roleUuid, roleName, existingClaimId, UUID.randomUUID());
        Mockito.when(roleRepositoryMock.findByUuid(roleDto.getUuid())).thenReturn(Optional.of(role));
        Role roleMock = Mockito.mock(Role.class);
        Mockito.when(roleRepositoryMock.saveAndFlush(Mockito.any(Role.class))).thenReturn(roleMock);

        /*********** Execute ************/
        roleServiceImpl.updateRole(roleDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(RoleHistory.class));
        //Mockito.verify(roleClaimRepositoryMock, Mockito.times(1)).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        //Mockito.verify(roleClaimRepositoryMock, Mockito.times(1)).saveAll(ArgumentMatchers.<RoleClaim>anyList());
    }

    @Test
    void deleteRole_ShouldPass_RoleNotAssignedToUsers() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();

        Long roleId = 1L;
        Long claimId = 1L;
        String roleName = "Manager";
        Role role = prepareRole(roleId, roleUuid, roleName, claimId, UUID.randomUUID());
        role.getUserRoles().clear();
        Mockito.when(roleRepositoryMock.findByUuid(roleUuid)).thenReturn(Optional.of(role));
        //Mockito.when(userRoleRepositoryMock.findByRole(role)).thenReturn(new ArrayList<>());

        /*********** Execute ************/
        roleServiceImpl.deleteRole(roleUuid);

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).findByUuid(roleUuid);
        //Mockito.verify(userRoleRepositoryMock, Mockito.times(1)).findByRole(role);
        //Mockito.verify(roleClaimRepositoryMock, Mockito.times(1)).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).delete(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(RoleHistory.class));
    }

    @Test
    void deleteRole_ShouldThrow_RoleAlreadyAssignedToUsers() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();

        Long roleId = 1L;
        Long claimId = 1L;
        String roleName = "Manager";
        Role role = prepareRole(roleId, roleUuid, roleName, claimId, UUID.randomUUID());
        Mockito.when(roleRepositoryMock.findByUuid(roleUuid)).thenReturn(Optional.of(role));

        UserRole userRole = Mockito.mock(UserRole.class);

        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(userRole);
        //Mockito.when(userRoleRepositoryMock.findByRole(role)).thenReturn(userRoles);

        /*********** Execute ************/
        Assertions.assertThrows(UserManagementException.class, () -> {
            roleServiceImpl.deleteRole(roleUuid);
        });

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).findByUuid(roleUuid);
        //Mockito.verify(userRoleRepositoryMock, Mockito.times(1)).findByRole(role);
        //Mockito.verify(roleClaimRepositoryMock, Mockito.never()).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        Mockito.verify(roleRepositoryMock, Mockito.never()).delete(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.never()).save(Mockito.any(RoleHistory.class));
    }

    @Test
    void deleteRole_ShouldThrow_RoleNotFoundException() {
        /*********** Setup ************/
        UUID roleUuid = UUID.randomUUID();

        Mockito.when(roleRepositoryMock.findByUuid(roleUuid)).thenReturn(Optional.empty());

        /*********** Execute ************/
        Assertions.assertThrows(RoleNotFoundException.class, () -> {
            roleServiceImpl.deleteRole(roleUuid);
        });

        /*********** Verify/Assertions ************/
        Mockito.verify(roleRepositoryMock, Mockito.times(1)).findByUuid(roleUuid);
        //Mockito.verify(userRoleRepositoryMock, Mockito.never()).findByRole(Mockito.any(Role.class));
        //Mockito.verify(roleClaimRepositoryMock, Mockito.never()).deleteAll(ArgumentMatchers.<RoleClaim>anyList());
        Mockito.verify(roleRepositoryMock, Mockito.never()).delete(Mockito.any(Role.class));
        Mockito.verify(roleHistoryRepositoryMock, Mockito.never()).save(Mockito.any(RoleHistory.class));
    }

    private List<Role> prepareRolesList() {
        List<Role> list = new ArrayList<>();
        list.add(prepareRole(1L, UUID.randomUUID(), "Manager", 1L, UUID.randomUUID()));
        list.add(prepareRole(2L, UUID.randomUUID(), "Admin", 2L, UUID.randomUUID()));
        return list;
    }

    private Role prepareRole(Long roleId, UUID roleUuid, String roleName, Long claimId, UUID claimUuid) {
        Role role = new Role();
        role.setId(roleId);
        role.setUuid(roleUuid);
        role.setName(roleName);

        Claim claim = prepareClaim(claimId, claimUuid, "UserGetAll");
        Set<RoleClaim> roleClaims = new HashSet<>();
        RoleClaim roleClaim = new RoleClaim(role, claim, PERFORMED_BY);
        roleClaims.add(roleClaim);
        role.setRoleClaims(roleClaims);

        User user = prepareUser(1L, UUID.randomUUID());
        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole(user, role, PERFORMED_BY);
        userRoles.add(userRole);
        role.setUserRoles(userRoles);

        return role;
    }

    private Claim prepareClaim(Long claimId, UUID claimUuid, String resourceName) {
        Claim claim = new Claim();
        claim.setId(claimId);
        claim.setUuid(claimUuid);
        claim.setResourceName(resourceName);
        return claim;
    }

    private User prepareUser(Long id, UUID uuid) {
        User user = new User();
        user.setId(id);
        user.setUuid(uuid);
        return user;
    }

    private RoleDto prepareRoleDto(UUID roleId, String roleName, UUID claimId) {
        RoleDto roleDto = new RoleDto();
        roleDto.setUuid(roleId);
        roleDto.setName(roleName);

        ClaimDto claim = new ClaimDto();
        claim.setUuid(claimId);
        claim.setResourceName("UserGetAll");

        List<ClaimDto> claims = new ArrayList<>();
        claims.add(claim);
        roleDto.setClaims(claims);
        return roleDto;
    }

    private RoleDto prepareRoleDto(String roleName) {
        RoleDto roleDto = new RoleDto();
        roleDto.setUuid(UUID.randomUUID());
        roleDto.setName(roleName);
        roleDto.setClaims(prepareClaimsList());
        return roleDto;
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

    private List<UUID> prepareClaimUuidList() {
        List<UUID> claims = new ArrayList<>();
        claims.add(UUID.randomUUID());
        claims.add(UUID.randomUUID());
        return claims;
    }

    private UpdateRoleDto prepareUpdateRoleDto(UUID roleId, String roleName, UUID claimId) {
        UpdateRoleDto updateRoleDto = new UpdateRoleDto();
        updateRoleDto.setUuid(roleId);
        updateRoleDto.setName(roleName);

        List<UUID> claims = new ArrayList<>();
        claims.add(claimId);
        updateRoleDto.setClaims(claims);
        return updateRoleDto;
    }

    private CreateRoleDto prepareCreateRoleDto(String roleName, UUID claimId) {
        CreateRoleDto createRoleDto = new CreateRoleDto();
        createRoleDto.setName(roleName);

        List<UUID> claims = new ArrayList<>();
        claims.add(claimId);
        createRoleDto.setClaims(claims);
        return createRoleDto;
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

}
