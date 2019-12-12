package com.oars.dao;

import com.oars.entity.Airport;
import com.oars.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findAllByTravelDateAndDepartureFrom(Date travelDate, Airport departureFrom);

    List<Flight> findAllByBusinessClassFareBetween(int from, int to);

    List<Flight> findAllByFirstclassFareBetween(int from, int to);

    List<Flight> findAllByEconomyClassFareBetween(int from, int to);

    List<Flight> findAllByAirline(String airline);

    List<Flight> findAllByAirlineOrBusinessClassFareBetween(String airline, int from, int to);

    List<Flight> findAllByAirlineOrFirstclassFareBetween(String airline, int from, int to);

    List<Flight> findAllByAirlineOrEconomyClassFareBetween(String airline, int from, int to);

    List<Flight> findAllByDepartureFromOrArrivalAt(Airport departureFrom, Airport arrivalAt);
}
