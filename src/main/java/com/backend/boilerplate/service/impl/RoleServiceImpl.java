package com.backend.boilerplate.service.impl;

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
import com.backend.boilerplate.entity.Status;
import com.backend.boilerplate.entity.UserRole;
import com.backend.boilerplate.exception.ClaimNotFoundException;
import com.backend.boilerplate.exception.RoleNotFoundException;
import com.backend.boilerplate.exception.UserManagementException;
import com.backend.boilerplate.modelmapper.ClaimMapper;
import com.backend.boilerplate.modelmapper.RoleMapper;
import com.backend.boilerplate.service.RoleService;
import com.backend.boilerplate.util.ErrorGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.backend.boilerplate.constant.Role.DEFAULT;
import static com.backend.boilerplate.constant.Role.SYSTEM_ADMIN;

/**
 * Implementation of {@link RoleService}.
 *
 * @author sarvesh
 * @version 0.0.2
 * @since 0.0.1
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private RoleClaimRepository roleClaimRepository;

    @Autowired
    private RoleHistoryRepository roleHistoryRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ClaimMapper claimMapper;

    //    @Autowired
    //    private IAuthenticationFacade authenticationFacade;

    @Autowired
    private UserRepository userRepository;

    private final TransactionTemplate transactionTemplate;

    public RoleServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
            .filter(role -> !(role.getName().equals(DEFAULT.getName())
                || role.getName().equals(SYSTEM_ADMIN.getName())))
            .map(role -> {
                RoleDto roleDto = roleMapper.convertToDto(role);
                List<Claim> claims = role.getRoleClaims().stream()
                    .map(RoleClaim::getClaim).collect(Collectors.toList());
                List<ClaimDto> claimDtos = claimMapper.convertToDtos(claims);
                roleDto.setClaims(claimDtos);
                return roleDto;
            }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleDto createRole(CreateRoleDto createRoleDto) {
        Long createdBy = -1l;
        //        userRepository.findIdByUuid(authenticationFacade.getUserId())
        //            .orElseThrow(InvalidUserException::new);

        Role role = roleMapper.convertToEntity(createRoleDto);
        role.setStatus(Status.CREATED);
        role.setPerformedBy(createdBy);

        List<RoleClaim> roleClaims = createRoleDto.getClaims().stream()
            .map(claimUuid -> claimRepository.findByUuid(claimUuid).orElseThrow(ClaimNotFoundException::new))
            .map(claim -> new RoleClaim(role, claim, createdBy))
            .collect(Collectors.toList());

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Role rolePersisted = roleRepository.saveAndFlush(role);
                RoleHistory roleHistory = RoleHistory.builder()
                    .id(new RoleHistory.RoleHistoryId(role.getId()))
                    .uuid(role.getUuid())
                    .name(role.getName())
                    .status(role.getStatus())
                    .performedBy(role.getPerformedBy())
                    .build();
                roleHistoryRepository.save(roleHistory);

                roleClaims.forEach(roleClaim -> roleClaim.setRole(rolePersisted));
                roleClaimRepository.saveAll(roleClaims);
            }
        });
        RoleDto roleDto = roleMapper.convertToDto(role);
        List<ClaimDto> claimDtos = role.getRoleClaims().stream()
            .map(RoleClaim::getClaim)
            .map(claim -> claimMapper.convertToClaimDto(claim))
            .collect(Collectors.toList());
        roleDto.setClaims(claimDtos);
        return roleDto;
    }

    @Override
    @Transactional
    public RoleDto updateRole(UpdateRoleDto updateRoleDto) {
        Long updatedBy = -1l;
        //        userRepository.findIdByUuid(authenticationFacade.getUserId())
        //            .orElseThrow(InvalidUserException::new);

        UUID uuid = updateRoleDto.getUuid();
        Role role = roleRepository.findByUuid(uuid)
            .orElseThrow(RoleNotFoundException::new);

        roleMapper.mergeToEntity(updateRoleDto, role);
        role.setStatus(Status.UPDATED);
        role.setPerformedBy(updatedBy);

        Set<RoleClaim> existingRoleClaims = role.getRoleClaims();
        Set<RoleClaim> newRoleClaims = updateRoleDto.getClaims().stream()
            .map(claimUuid -> claimRepository.findByUuid(claimUuid).orElseThrow(ClaimNotFoundException::new))
            .map(claim -> new RoleClaim(role, claim, updatedBy))
            .collect(Collectors.toSet());

        //        List<RoleClaim> existingRoleClaimsToDelete = new ArrayList<>(existingRoleClaims);
        //        existingRoleClaimsToDelete.removeAll(newRoleClaims);
        //        newRoleClaims.removeAll(existingRoleClaims);
        role.getRoleClaims().clear();
        role.getRoleClaims().addAll(newRoleClaims);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                roleRepository.saveAndFlush(role);
                RoleHistory roleHistory = RoleHistory.builder()
                    .id(new RoleHistory.RoleHistoryId(role.getId()))
                    .uuid(role.getUuid())
                    .name(role.getName())
                    .status(role.getStatus())
                    .performedBy(role.getPerformedBy())
                    .build();
                roleHistoryRepository.save(roleHistory);

                //                if (!existingRoleClaimsToDelete.isEmpty()) {
                //                    roleClaimRepository.deleteAll(existingRoleClaimsToDelete);
                //                }
                //                if (!newRoleClaims.isEmpty()) {
                //                    roleClaimRepository.saveAll(newRoleClaims);
                //                }
            }
        });
        RoleDto roleDto = roleMapper.convertToDto(role);
        List<ClaimDto> claimDtos = role.getRoleClaims().stream()
            .map(RoleClaim::getClaim)
            .map(claim -> claimMapper.convertToClaimDto(claim))
            .collect(Collectors.toList());
        roleDto.setClaims(claimDtos);
        return roleDto;
    }

    @Override
    @Transactional
    public void deleteRole(UUID uuid) {
        Long deletedBy = -1l;
        //        userRepository.findIdByUuid(authenticationFacade.getUserId())
        //            .orElseThrow(InvalidUserException::new);

        Role role = roleRepository.findByUuid(uuid)
            .orElseThrow(RoleNotFoundException::new);
        role.setStatus(Status.DELETED);
        role.setPerformedBy(deletedBy);

        Set<UserRole> userRoles = role.getUserRoles();
        if (!userRoles.isEmpty()) {
            throw new UserManagementException(ErrorGenerator.generateForCode("1021"));
        }

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                roleClaimRepository.deleteAll(role.getRoleClaims());
                roleRepository.delete(role);
                RoleHistory roleHistory = RoleHistory.builder()
                    .id(new RoleHistory.RoleHistoryId(role.getId()))
                    .uuid(role.getUuid())
                    .name(role.getName())
                    .status(role.getStatus())
                    .performedBy(role.getPerformedBy())
                    .build();
                roleHistoryRepository.save(roleHistory);
            }
        });
    }
}
