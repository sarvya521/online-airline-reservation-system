package com.backend.boilerplate.service;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
public interface PaginationService<T, P> {

    void setSortingParameters();

    void setDefaultSortParameter();

    P getPageDto(Integer pageNo, Integer pageSize, String sortBy, boolean isAscending);
}
