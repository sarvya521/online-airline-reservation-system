package com.backend.boilerplate.service.impl;

import com.backend.boilerplate.config.ModelMapperConfig;
import com.backend.boilerplate.dao.RoleRepository;
import com.backend.boilerplate.dao.UserHistoryRepository;
import com.backend.boilerplate.dao.UserRepository;
import com.backend.boilerplate.dao.UserRoleRepository;
import com.backend.boilerplate.dto.CreateUserDto;
import com.backend.boilerplate.dto.UpdateUserDto;
import com.backend.boilerplate.dto.UserDto;
import com.backend.boilerplate.dto.UserRoleDto;
import com.backend.boilerplate.entity.Claim;
import com.backend.boilerplate.entity.Role;
import com.backend.boilerplate.entity.RoleClaim;
import com.backend.boilerplate.entity.User;
import com.backend.boilerplate.entity.UserHistory;
import com.backend.boilerplate.entity.UserRole;
import com.backend.boilerplate.exception.UserNotFoundException;
import com.backend.boilerplate.modelmapper.RoleMapper;
import com.backend.boilerplate.modelmapper.UserMapper;
import com.backend.boilerplate.util.ErrorGeneratorInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static com.backend.boilerplate.constant.Role.DEFAULT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author sarvesh
 * @version 0.0.2
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@Import({UserMapper.class, RoleMapper.class, ModelMapper.class, ModelMapperConfig.class,
    ErrorGeneratorInitializer.class})
public class UserServiceImplTest {

    private static final Long PERFORMED_BY = 1L;
    UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private UserHistoryRepository userHistoryRepositoryMock;
    @Mock
    private RoleRepository roleRepositoryMock;
    @Mock
    private UserRoleRepository userRoleRepositoryMock;
    @Mock
    private PlatformTransactionManager platformTransactionManagerMock;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * Setup for before each test case
     */
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userServiceImpl = new UserServiceImpl(platformTransactionManagerMock);

        ReflectionTestUtils.setField(userServiceImpl, "userRepository", userRepositoryMock);
        ReflectionTestUtils.setField(userServiceImpl, "userHistoryRepository", userHistoryRepositoryMock);
        ReflectionTestUtils.setField(userServiceImpl, "roleRepository", roleRepositoryMock);
        ReflectionTestUtils.setField(userServiceImpl, "userRoleRepository", userRoleRepositoryMock);
        ReflectionTestUtils.setField(userServiceImpl, "userMapper", userMapper);
        ReflectionTestUtils.setField(userServiceImpl, "roleMapper", roleMapper);

        UUID performedByUUID = UUID.randomUUID();
        Mockito.when(userRepositoryMock.findIdByUuid(performedByUUID)).thenReturn(Optional.of(1L));
    }

    @Test
    void getUserById_shouldPass_userFound() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        Optional<User> userOptional = Optional.of(prepareUser(userId, roleId));
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(userOptional);

        /*********** Execute ************/
        UserDto userDto = userServiceImpl.getUserByUuid(userId);

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByUuid(userId);
        assertNotNull(userDto);
        assertEquals(userId.toString(), userDto.getUuid().toString());
        assertEquals(userOptional.get().getFirstName(), userDto.getFirstName());
        assertEquals(userOptional.get().getLastName(), userDto.getLastName());
        assertEquals(userOptional.get().getEmail(), userDto.getEmail());
        assertNotNull(userDto.getRoles());
        assertEquals(1, userDto.getRoles().size());
        UserRoleDto roleDto = userDto.getRoles().get(0);
        assertEquals(roleId.toString(), roleDto.getUuid().toString());
        assertEquals(userOptional.get().getUserRoles().stream().findFirst().get().getRole().getName(), roleDto.getName());
    }

    @Test
    void getUserById_shouldPass_filterDefaultRole() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        User user = prepareUser(userId, roleId);
        Role role = new Role();
        role.setId(2L);
        role.setUuid(UUID.randomUUID());
        role.setName(DEFAULT.getName());
        UserRole userRole = new UserRole(user, role, PERFORMED_BY);
        user.getUserRoles().add(userRole);
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(Optional.of(user));

        /*********** Execute ************/
        UserDto userDto = userServiceImpl.getUserByUuid(userId);

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByUuid(userId);
        assertNotNull(userDto);
        assertFalse(userDto.getRoles().stream().anyMatch(userRoleDto -> userRoleDto.getName().equals(DEFAULT.getName())));
    }

    @Test
    void getUserById_shouldThrow_userNotFound() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        Optional<User> userOptional = Optional.empty();
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(userOptional);

        /*********** Execute ************/
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userServiceImpl.getUserByUuid(userId);
        });

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByUuid(userId);
    }

    @Test
    void createUser_shouldPass() {
        /*********** Setup ************/
        UUID roleId = UUID.randomUUID();

        CreateUserDto createUserDto = prepareCreateUserDto(roleId);
        mockRole(createUserDto.getRoles(), "Random Claim");

        Role role = new Role();
        role.setId(1L);
        role.setUuid(roleId);
        role.setName("mockRole");

        Role defaultRole = new Role();
        defaultRole.setId(2L);
        defaultRole.setUuid(UUID.randomUUID());
        defaultRole.setName(DEFAULT.getName());

        User user = prepareUser(UUID.randomUUID(), roleId);
        Mockito.when(userRepositoryMock.saveAndFlush(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(roleRepositoryMock.findByUuid(Mockito.any(UUID.class))).thenReturn(Optional.of(role));
        Mockito.when(roleRepositoryMock.findByNameIgnoreCase(DEFAULT.getName())).thenReturn(Optional.of(defaultRole));

        /*********** Execute ************/
        userServiceImpl.createUser(createUserDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(User.class));
        Mockito.verify(userHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(UserHistory.class));
    }

    @Test
    void updateUser_shouldPass() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        User user = prepareUser(userId, roleId);
        Optional<User> userOptional = Optional.of(user);
        Optional<Role> roleOptional = Optional.of(user.getUserRoles().stream().findFirst().get().getRole());
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(userOptional);
        Mockito.when(roleRepositoryMock.findByUuid(roleId)).thenReturn(roleOptional);

        UpdateUserDto updateUserDto = prepareUpdateUserDto(userId, roleId);
        /*********** Execute ************/
        userServiceImpl.updateUser(updateUserDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByUuid(userId);
        Mockito.verify(userRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(User.class));
        Mockito.verify(userHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(UserHistory.class));
    }

    @Test
    void updateUser_shouldPass_addNewAndDeleteExistingUserRoles() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        UUID newRoleId = UUID.randomUUID();
        User user = prepareUser(userId, roleId);
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(userOptional);

        Role newRole = new Role();
        newRole.setId(1L);
        newRole.setUuid(newRoleId);
        newRole.setName("mockRole");

        Role defaultRole = new Role();
        defaultRole.setId(2L);
        defaultRole.setUuid(UUID.randomUUID());
        defaultRole.setName(DEFAULT.getName());

        Mockito.when(roleRepositoryMock.findByUuid(newRoleId)).thenReturn(Optional.of(newRole));

        UpdateUserDto updateUserDto = prepareUpdateUserDto(userId, roleId);
        updateUserDto.getRoles().clear();
        updateUserDto.getRoles().add(newRoleId);
        /*********** Execute ************/
        userServiceImpl.updateUser(updateUserDto);

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByUuid(userId);
        Mockito.verify(userRepositoryMock, Mockito.times(1)).saveAndFlush(Mockito.any(User.class));
        Mockito.verify(userHistoryRepositoryMock, Mockito.times(1)).save(Mockito.any(UserHistory.class));
        //Mockito.verify(userRoleRepositoryMock, Mockito.times(1)).deleteAll(Mockito.<UserRole>anyList());
        //Mockito.verify(userRoleRepositoryMock, Mockito.times(1)).saveAll(Mockito.<UserRole>anyList());
    }

    @Test
    void updateUser_shouldThrow_userNotFoundException() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        Optional<User> userOptional = Optional.empty();
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(userOptional);

        UpdateUserDto updateUserDto = prepareUpdateUserDto(userId, roleId);
        /*********** Execute ************/
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userServiceImpl.updateUser(updateUserDto);
        });

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByUuid(userId);
        Mockito.verify(userRepositoryMock, Mockito.never()).saveAndFlush(Mockito.any(User.class));
        Mockito.verify(userHistoryRepositoryMock, Mockito.never()).save(Mockito.any(UserHistory.class));
    }

    @Test
    void deleteUser_shouldPass() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();
        User user = prepareUser(userId, roleId);
        user.getUserRoles().add(new UserRole(user, prepareMockRole("NonAdmin Claim"), PERFORMED_BY));
        Optional<User> userOptional = Optional.of(user);
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(userOptional);

        /*********** Execute ************/
        userServiceImpl.deleteUser(userId);

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).delete(Mockito.any(User.class));
    }

    @Test
    void deleteUser_shouldThrow_userNotFoundException() {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        Optional<User> userOptional = Optional.empty();
        Mockito.when(userRepositoryMock.findByUuid(userId)).thenReturn(userOptional);

        /*********** Execute ************/
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            userServiceImpl.deleteUser(userId);
        });

        /*********** Verify/Assertions ************/
        Mockito.verify(userRepositoryMock, Mockito.times(1)).findByUuid(userId);
        Mockito.verify(userRepositoryMock, Mockito.never()).delete(Mockito.any(User.class));
        Mockito.verify(userHistoryRepositoryMock, Mockito.never()).save(Mockito.any(UserHistory.class));
    }

    private User prepareUser(UUID userUuid, UUID roleUuid) {
        User user = new User();
        user.setId(1L);
        user.setUuid(userUuid);
        user.setEmail("xyz@xyz.com");
        user.setFirstName("John");
        user.setLastName("Wick");
        Role role = new Role();
        role.setId(1L);
        role.setUuid(roleUuid);
        role.setName("Physician");
        UserRole userRole = new UserRole(user, role, PERFORMED_BY);
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRole);
        user.setUserRoles(userRoles);
        return user;
    }

    private CreateUserDto prepareCreateUserDto(UUID roleId) {
        CreateUserDto userDto = new CreateUserDto();
        userDto.setEmail("xyz@xyz.com");
        userDto.setFirstName("John");
        userDto.setLastName("Wick");
        List<UUID> roles = new ArrayList<>();
        roles.add(roleId);
        userDto.setRoles(roles);
        return userDto;
    }

    private UpdateUserDto prepareUpdateUserDto(UUID userId, UUID roleId) {
        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setUuid(userId);
        userDto.setEmail("xyz@xyz.com");
        userDto.setFirstName("John");
        userDto.setLastName("Wick");
        List<UUID> roles = new ArrayList<>();
        roles.add(roleId);
        userDto.setRoles(roles);
        return userDto;
    }

    private void mockRole(Iterable<UUID> roleIds, String claimName) {
        List<Role> roles = new ArrayList<>();
        roles.add(prepareMockRole(claimName));
        Mockito.when(roleRepositoryMock.findAllByUuidIn(roleIds)).thenReturn(roles);
    }

    private Role prepareMockRole(String claimName) {
        Role role = Mockito.mock(Role.class);
        Set<RoleClaim> roleClaims = new HashSet<>();
        RoleClaim roleClaim = Mockito.mock(RoleClaim.class);
        Claim claimMock = Mockito.mock(Claim.class);
        Mockito.when(claimMock.getResourceName()).thenReturn(claimName);
        Mockito.when(roleClaim.getClaim()).thenReturn(claimMock);
        roleClaims.add(roleClaim);
        Mockito.when(role.getRoleClaims()).thenReturn(roleClaims);
        return role;
    }
}