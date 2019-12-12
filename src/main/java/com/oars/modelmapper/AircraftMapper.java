package com.oars.modelmapper;

import com.oars.dto.AircraftDto;
import com.oars.entity.Aircraft;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AircraftMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Convert {@link Aircraft} entity to {@link AircraftDto }
     *
     * @param aircraft
     * @return {@link AircraftDto }
     */
    public AircraftDto convertToDto(Aircraft aircraft) {
        return modelMapper.map(aircraft, AircraftDto.class);
    }

    /**
     * Convert {@link AircraftDto} entity to {@link Aircraft }
     *
     * @param aircraftDto
     * @return {@link Aircraft }
     */
    public Aircraft convertToEntity(AircraftDto aircraftDto) {
        return modelMapper.map(aircraftDto, Aircraft.class);
    }

    /**
     * Merge {@link AircraftDto} entity to {@link Aircraft }
     *
     * @param aircraftDto
     * @return {@link Aircraft }
     */
    public void mergeToEntity(AircraftDto aircraftDto, Aircraft aircraft) {
        modelMapper.map(aircraftDto, aircraft);
    }
}
