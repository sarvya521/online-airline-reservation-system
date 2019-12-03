package com.backend.boilerplate.dao;

import com.backend.boilerplate.entity.RoleHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author sarvesh
 * @version 0.0.1
 * @since 0.0.1
 */
@Repository
public interface RoleHistoryRepository extends JpaRepository<RoleHistory, Long> {

}
