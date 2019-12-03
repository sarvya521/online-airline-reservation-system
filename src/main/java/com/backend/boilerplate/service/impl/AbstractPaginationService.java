package com.backend.boilerplate.service.impl;

import com.backend.boilerplate.service.PaginationService;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Log4j2
public abstract class AbstractPaginationService<T, P> implements PaginationService {

    @Getter
    @Value("${spring.data.web.pageable.default-page-size}")
    private Integer defaultPageSize;

    @Getter
    @Value("${spring.data.web.pageable.max-page-size}")
    private Integer maxPageSize;

    protected String defaultSort;

    protected Set<String> sortParameters;

    @PostConstruct
    void init() {
        this.setSortingParameters();
        this.setDefaultSortParameter();
        Objects.requireNonNull(this.sortParameters, "sort parameters are not configured");
        Objects.requireNonNull(this.defaultSort, "default sort parameter is not configured");
        Objects.requireNonNull(this.defaultPageSize, "default page size is not configured");
        Objects.requireNonNull(this.maxPageSize, "max page size is not configured");
    }

    Pageable getPageable(final Optional<Integer> pageNoOptional, final Optional<Integer> pageSizeOptional,
                         final Optional<String> sortByOptional, final boolean isAscendingOrder) {
        int pageNo = pageNoOptional.orElse(0);
        int pageSize = pageSizeOptional.orElse(defaultPageSize);
        if (pageSize > maxPageSize) {
            log.warn("Input PageSize {} is greater than maxPageSize", pageSize);
            pageSize = maxPageSize;
        }
        String sortBy = null;
        if (sortByOptional.isPresent()) {
            sortBy = sortByOptional.get();
        }
        if (!sortParameters.contains(sortBy)) {
            log.warn("Input sortBy {} is not found in configured sortParameters {}", sortBy, sortParameters);
            sortBy = defaultSort;
        }
        if (isAscendingOrder) {
            return PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());
        } else {
            return PageRequest.of(pageNo, pageSize, Sort.by(sortBy).descending());
        }
    }

}
