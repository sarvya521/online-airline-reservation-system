package com.oars.service;

import com.oars.dto.AirportDto;
import com.oars.dto.FlightDto;

import java.util.Date;
import java.util.List;

public interface FlightService {
    FlightDto createFlight(FlightDto flightDto);

    FlightDto updateFlight(FlightDto flightDto);

    void deleteFlight(Long id);

    List<FlightDto> findAllByTravelDateAndDepartureFrom(Date travelDate, AirportDto departureFrom);

    List<FlightDto> findAllByBusinessClassFareBetween(int from, int to);

    List<FlightDto> findAllByFirstclassFareBetween(int from, int to);

    List<FlightDto> findAllByEconomyClassFareBetween(int from, int to);

    List<FlightDto> findAllByAirline(String airline);

    List<FlightDto> findAllByAirlineOrBusinessClassFareBetween(String airline, int from, int to);

    List<FlightDto> findAllByAirlineOrFirstclassFareBetween(String airline, int from, int to);

    List<FlightDto> findAllByAirlineOrEconomyClassFareBetween(String airline, int from, int to);

    List<FlightDto> findAllByDepartureFromOrArrivalAt(AirportDto departureFrom, AirportDto arrivalAt);
}
