package com.backend.boilerplate.service.impl;

import com.backend.boilerplate.config.ApplicationProperties;
import com.backend.boilerplate.dao.UserRepository;
import com.backend.boilerplate.dto.UserDto;
import com.backend.boilerplate.dto.UserPageDto;
import com.backend.boilerplate.entity.User;
import com.backend.boilerplate.service.UserService;
import com.backend.boilerplate.util.FunctionalReadWriteLock;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
@Service("UserPaginationService")
public class UserPaginationServiceImpl extends AbstractPaginationService<User, UserPageDto> {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private final AtomicLong totalRecords = new AtomicLong(0);

    private final FunctionalReadWriteLock totalRecordsGuard = new FunctionalReadWriteLock();

    @Override
    public void setSortingParameters() {
        this.sortParameters = applicationProperties.getSort().getUser().getParams();
    }

    @Override
    public void setDefaultSortParameter() {
        this.defaultSort = applicationProperties.getSort().getUser().getDefaultParam();
    }

    @Override
    @Transactional
    public UserPageDto getPageDto(final Integer pageNo, final Integer pageSize, final String sortBy,
                                  final boolean isAscending) {
        UserPageDto userPageDto = new UserPageDto();
        if (!isValidPageNo(pageNo, pageSize)) {
            return userPageDto;
        }
        Pageable pageable = this.getPageable(Optional.ofNullable(pageNo),
            Optional.ofNullable(pageSize), Optional.ofNullable(sortBy), isAscending);
        Page<User> pagedResult = userRepository.findAll(pageable);
        if (pagedResult.hasContent()) {
            final List<User> users = pagedResult.getContent();
            List<UserDto> userList = users.stream()
                .map(user -> userService.prepareUserDto(user))
                .collect(Collectors.toList());
            userPageDto.setUsers(userList);
            totalRecordsGuard.write(() -> totalRecords.set(pagedResult.getTotalElements()));
            totalRecordsGuard.read(() -> userPageDto.setTotalRecords(totalRecords.get()));
        }
        return userPageDto;
    }

    private boolean isValidPageNo(Integer pageNo, Integer pageSize) {
        if (Objects.nonNull(pageNo)) {
            if (Objects.isNull(pageSize)) {
                pageSize = this.getDefaultPageSize();
            }
            AtomicLong totalPages = new AtomicLong();
            Integer finalPageSize = pageSize;
            totalRecordsGuard.read(() -> totalPages.set((totalRecords.get() / finalPageSize) + 1));
            if (pageNo > totalPages.get()) {
                totalRecordsGuard.write(() -> {
                    long totalElements = userRepository.count();
                    totalRecords.set(totalElements);
                    totalPages.set((totalRecords.get() / finalPageSize) + 1);
                });
                return pageNo <= totalPages.get();
            }
        }
        return true;
    }
}
