package com.oars.dao;

import com.oars.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNameIgnoreCase(String name);

    List<Role> findAllByIdIn(Iterable<Long> ids);

    Optional<Long> countByNameIgnoreCase(String name);

    Optional<Long> countByIdNotAndNameIgnoreCase(Long id, String name);

    List<Role> findByNameIn(List<String> names);
}
