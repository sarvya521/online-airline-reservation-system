package com.oars.service;

import com.oars.dto.AircraftDto;

public interface AircraftService {

    AircraftDto createAircraft(AircraftDto aircraftDto);

    AircraftDto updateAircraft(AircraftDto aircraftDto);

    void deleteAircraft(Long id);

    boolean checkIfAircraftExists(String email);
}
