package com.oars.service;

import com.oars.dto.AircraftDto;

import java.util.List;

public interface AircraftService {

    List<AircraftDto> getAllAircraft();

    AircraftDto createAircraft(AircraftDto aircraftDto);

    AircraftDto updateAircraft(AircraftDto aircraftDto);

    void deleteAircraft(Long id);

    boolean checkIfAircraftExists(String email);
}
