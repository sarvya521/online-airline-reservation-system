package com.oars.service;

import com.oars.dto.AirportDto;

import java.util.List;

public interface AirportService {

    List<AirportDto> getAllAirport();

    AirportDto createAirport(AirportDto airportDto);

    AirportDto updateAirport(AirportDto airportDto);

    void deleteAirport(Long id);

    boolean checkIfAirportExists(String name, String alias);
}
