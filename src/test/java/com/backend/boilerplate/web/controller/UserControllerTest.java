package com.backend.boilerplate.web.controller;

import com.backend.boilerplate.TestLocalValidatorFactoryBean;
import com.backend.boilerplate.config.ErrorMessageSourceConfig;
import com.backend.boilerplate.dao.UserRepository;
import com.backend.boilerplate.dto.CreateUserDto;
import com.backend.boilerplate.dto.UpdateUserDto;
import com.backend.boilerplate.dto.UserDto;
import com.backend.boilerplate.dto.UserPageDto;
import com.backend.boilerplate.dto.UserRoleDto;
import com.backend.boilerplate.service.UserService;
import com.backend.boilerplate.service.impl.UserPaginationServiceImpl;
import com.backend.boilerplate.util.ErrorGeneratorInitializer;
import com.backend.boilerplate.web.exception.UserManagementExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import java.util.stream.IntStream;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
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
public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @MockBean
    private UserService userService;

    @Mock
    private UserPaginationServiceImpl userPaginationService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockServletContext servletContext;

    @Autowired
    private UserManagementExceptionHandler owUserManagementExceptionHandler;

    /**
     * Setup for before each test case
     */
    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        Map<String, JpaRepository> repositories = new HashMap<>();
        repositories.put("userRepository", this.userRepository);
        LocalValidatorFactoryBean validatorFactoryBean = new TestLocalValidatorFactoryBean(servletContext,
            repositories);

        mockMvc = standaloneSetup(userController)
            .setValidator(validatorFactoryBean)
            .setControllerAdvice(owUserManagementExceptionHandler)
            .build();
    }

    /**
     * @throws Exception
     * @since 0.0.1
     */
    @Test
    public void getUsers_FetchTwoUsers_SortedByFirstNameForFirstPageWithSizeTwo() throws Exception {

        /*********** Setup ************/
        Integer pageNo = 0;
        Integer pageSize = 2;
        String sortBy = "firstName";
        boolean isAscendingOrder = true;
        List<UserDto> users =
            preparePaginatedUsersList().stream()
                .sorted((u1, u2) -> u1.getFirstName().compareTo(u2.getFirstName()))
                .limit(pageSize)
                .collect(Collectors.toList());
        UserPageDto userPage = new UserPageDto();
        userPage.setUsers(users);
        userPage.setTotalRecords((long) users.size());
        Mockito.when(userPaginationService.getPageDto(pageNo, pageSize, sortBy, isAscendingOrder)).thenReturn(userPage);

        /*********** Execute ************/
        mockMvc.perform(get("/api/v1/user?pageNo=" + pageNo + "&pageSize=" + pageSize + "&sortBy=" + sortBy)
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data.totalRecords", is(2)))
            .andExpect(jsonPath("$.data.users", hasSize(2)))
            .andExpect(jsonPath("$.data.users[0].email", is("Axyz@xyz.com")))
            .andExpect(jsonPath("$.data.users[0].firstName", is("AJohn")))
            .andExpect(jsonPath("$.data.users[0].lastName", is("AWick")))
            .andExpect(jsonPath("$.data.users[0].roles[0].name", is("Manager")))
            .andExpect(jsonPath("$.data.users[1].email", is("Bxyz@xyz.com")))
            .andExpect(jsonPath("$.data.users[1].firstName", is("BJohn")))
            .andExpect(jsonPath("$.data.users[1].lastName", is("BWick")))
            .andExpect(jsonPath("$.data.users[1].roles[0].name", is("Manager")))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(userPaginationService, times(1)).getPageDto(pageNo, pageSize, sortBy, isAscendingOrder);
        verifyNoMoreInteractions(userPaginationService);
    }

    /**
     * @throws Exception
     * @since 0.0.1
     */
    @Test
    public void getUsers_ShouldPass_EmptyList() throws Exception {

        /*********** Setup ************/
        Integer pageNo = null;
        Integer pageSize = null;
        String sortBy = null;
        boolean isAscendingOrder = true;
        UserPageDto userPage = new UserPageDto();
        Mockito.when(userPaginationService.getPageDto(pageNo, pageSize, sortBy, isAscendingOrder)).thenReturn(userPage);

        /*********** Execute ************/
        mockMvc.perform(get("/api/v1/user")
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data.totalRecords", is(0)))
            .andExpect(jsonPath("$.data.users", hasSize(0)))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(userPaginationService, times(1)).getPageDto(pageNo, pageSize, sortBy, isAscendingOrder);
        verifyNoMoreInteractions(userPaginationService);
    }

    @Test
    public void getUserById_ShouldPass_FetchUserDetails() throws Exception {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UserDto user = prepareUsersData(userId);
        Mockito.when(userService.getUserByUuid(userId)).thenReturn(user);

        /*********** Execute ************/
        mockMvc.perform(get("/api/v1/user/" + userId)
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data.uuid", is(userId.toString())))
            .andExpect(jsonPath("$.data.email", is("xyz@xyz.com")))
            .andExpect(jsonPath("$.data.firstName", is("John")))
            .andExpect(jsonPath("$.data.lastName", is("Wick")))
            .andExpect(jsonPath("$.data.roles[0].uuid", is(userId.toString())))
            .andExpect(jsonPath("$.data.roles[0].name", is("Manager")))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(userService, times(1)).getUserByUuid(userId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void createUser_ShouldPass_WithUserDetails() throws Exception {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        CreateUserDto createUserDto = prepareCreateUserData();
        UserDto userDto = prepareUsersData(userId);
        Mockito.when(userService.createUser(createUserDto)).thenReturn(userDto);
        Mockito.when(userRepository.countByEmailIgnoreCase(createUserDto.getEmail())).thenReturn(Optional.of(0L));

        /*********** Execute ************/
        mockMvc.perform(post("/api/v1/user").content(new ObjectMapper().writeValueAsBytes(createUserDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(201)))
            .andExpect(jsonPath("$.data.uuid", is(userId.toString())))
            .andExpect(jsonPath("$.data.email", is("xyz@xyz.com")))
            .andExpect(jsonPath("$.data.firstName", is("John")))
            .andExpect(jsonPath("$.data.lastName", is("Wick")))
            .andExpect(jsonPath("$.data.roles[0].uuid", is(userId.toString())))
            .andExpect(jsonPath("$.data.roles[0].name", is("Manager")))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(userService, times(1)).createUser(createUserDto);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void updateUser_ShouldPass_WithUserDetails() throws Exception {
        /*********** Setup ************/
        UUID userId = UUID.randomUUID();
        UpdateUserDto updateUserDto = prepareUpdateUsersData(userId);
        UserDto userDto = prepareUsersData(userId);
        Mockito.when(userService.updateUser(updateUserDto)).thenReturn(userDto);
        Mockito.when(userRepository.countByUuid(updateUserDto.getUuid())).thenReturn(Optional.of(1L));
        Mockito.when(userRepository.countByUuidNotAndEmailIgnoreCase(updateUserDto.getUuid(),
            updateUserDto.getEmail())).thenReturn(Optional.of(0L));

        /*********** Execute ************/
        mockMvc.perform(put("/api/v1/user/").content(new ObjectMapper().writeValueAsBytes(updateUserDto))
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)))
            .andExpect(jsonPath("$.data.uuid", is(userId.toString())))
            .andExpect(jsonPath("$.data.email", is("xyz@xyz.com")))
            .andExpect(jsonPath("$.data.firstName", is("John")))
            .andExpect(jsonPath("$.data.lastName", is("Wick")))
            .andExpect(jsonPath("$.data.roles[0].uuid", is(userId.toString())))
            .andExpect(jsonPath("$.data.roles[0].name", is("Manager")))
            .andExpect(jsonPath("$.errors").doesNotExist());

        /*********** Verify/Assertions ************/
        verify(userService, times(1)).updateUser(updateUserDto);
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void deleteUser_ShouldPass_WithUserId() throws Exception {
        UUID userId = UUID.randomUUID();

        /*********** Setup ************/
        Mockito.doNothing().when(userService).deleteUser(userId);

        /*********** Execute ************/
        mockMvc.perform(delete("/api/v1/user/" + userId)
            .contentType(APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("SUCCESS")))
            .andExpect(jsonPath("$.code", is(200)));

        /*********** Verify/Assertions ************/
        verify(userService, times(1)).deleteUser(userId);
        verifyNoMoreInteractions(userService);
    }

    /**
     * @return List<UserDto> list of {@link UserDto}
     * @since 0.0.1
     */
    private List<UserDto> preparePaginatedUsersList() {
        return IntStream.range(65, 91).mapToObj(index -> {
            UserDto userDto = prepareUsersData(UUID.randomUUID());
            userDto.setFirstName((char) index + userDto.getFirstName());
            userDto.setLastName((char) index + userDto.getLastName());
            userDto.setEmail((char) index + userDto.getEmail());
            return userDto;
        }).collect(Collectors.toList());
    }

    private List<UserDto> prepareUsersList() {
        List<UserDto> list = new ArrayList<>();
        list.add(prepareUsersData(UUID.randomUUID()));
        list.add(prepareUsersData(UUID.randomUUID()));
        return list;
    }

    private UserDto prepareUsersData(UUID id) {
        UserDto userDto = new UserDto();
        userDto.setUuid(id);
        userDto.setEmail("xyz@xyz.com");
        userDto.setFirstName("John");
        userDto.setLastName("Wick");
        UserRoleDto roleDto = new UserRoleDto();
        roleDto.setUuid(id);
        roleDto.setName("Manager");
        List<UserRoleDto> roles = new ArrayList<>();
        roles.add(roleDto);
        userDto.setRoles(roles);
        return userDto;
    }

    private UpdateUserDto prepareUpdateUsersData(UUID id) {
        UpdateUserDto userDto = new UpdateUserDto();
        userDto.setUuid(id);
        userDto.setEmail("xyz@xyz.com");
        userDto.setFirstName("John");
        userDto.setLastName("Wick");
        List<UUID> roles = new ArrayList<>();
        roles.add(UUID.randomUUID());
        userDto.setRoles(roles);
        return userDto;
    }

    private CreateUserDto prepareCreateUserData() {
        CreateUserDto userDto = new CreateUserDto();
        userDto.setEmail("xyz@xyz.com");
        userDto.setFirstName("John");
        userDto.setLastName("Wick");
        List<UUID> roles = new ArrayList<>();
        roles.add(UUID.randomUUID());
        userDto.setRoles(roles);
        return userDto;
    }
}
