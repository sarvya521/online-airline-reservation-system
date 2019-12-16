package com.oars.service;

import com.oars.dto.AirportDto;
import com.oars.dto.FlightDto;
import com.oars.dto.SearchFlightDto;

import java.time.LocalDate;
import java.util.List;

public interface FlightService {
    FlightDto createFlight(FlightDto flightDto);

    FlightDto updateFlight(FlightDto flightDto);

    void deleteFlight(Long id);

    List<FlightDto> search(SearchFlightDto searchFlightDto, boolean forReturningJourney);

    List<FlightDto> findAllByTravelDateAndDepartureFromAndArrivalAt(LocalDate travelDate,
                                                                    Long departureAirportId,
                                                                    Long arrivalAirportId);

    List<FlightDto> findAllByBusinessClassFareBetween(int from, int to);

    List<FlightDto> findAllByFirstclassFareBetween(int from, int to);

    List<FlightDto> findAllByEconomyClassFareBetween(int from, int to);

    List<FlightDto> findAllByAirline(String airline);

    List<FlightDto> findAllByAirlineOrBusinessClassFareBetween(String airline, int from, int to);

    List<FlightDto> findAllByAirlineOrFirstclassFareBetween(String airline, int from, int to);

    List<FlightDto> findAllByAirlineOrEconomyClassFareBetween(String airline, int from, int to);

    List<FlightDto> findAllByDepartureFromOrArrivalAt(AirportDto departureFrom, AirportDto arrivalAt);

    List<Integer> getSortedFares(List<FlightDto> flights, SearchFlightDto searchFlightDto);

    List<String> getAirlines(List<FlightDto> flights);

    List<FlightDto> getAllFlights();
}
