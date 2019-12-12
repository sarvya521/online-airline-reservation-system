package com.oars.service;

import com.oars.dto.AirportDto;

public interface AirportService {

    AirportDto createAirport(AirportDto airportDto);

    AirportDto updateAirport(AirportDto airportDto);

    void deleteAirport(Long id);

    boolean checkIfAirportExists(String name, String alias);
}
