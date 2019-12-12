package com.oars.dao;

import com.oars.entity.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {

    boolean existsByName(String name);

    boolean existsByAlias(String alias);
}
