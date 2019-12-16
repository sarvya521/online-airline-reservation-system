package com.oars.dao;

import com.oars.entity.Airport;
import com.oars.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByTravelDateAndDepartureFromAndArrivalAt(LocalDate travelDate, Airport departureFrom,
                                                              Airport arrivalAt);

    List<Flight> findByBusinessClassFareBetween(int from, int to);

    List<Flight> findByFirstclassFareBetween(int from, int to);

    List<Flight> findByEconomyClassFareBetween(int from, int to);

    List<Flight> findByAirline(String airline);

    List<Flight> findByAirlineOrBusinessClassFareBetween(String airline, int from, int to);

    List<Flight> findByAirlineOrFirstclassFareBetween(String airline, int from, int to);

    List<Flight> findByAirlineOrEconomyClassFareBetween(String airline, int from, int to);

    List<Flight> findByDepartureFromOrArrivalAt(Airport departureFrom, Airport arrivalAt);
}
