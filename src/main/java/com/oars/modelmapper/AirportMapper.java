package com.oars.modelmapper;

import com.oars.dto.AirportDto;
import com.oars.entity.Airport;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AirportMapper {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Convert {@link Airport} entity to {@link AirportDto }
     *
     * @param airport
     * @return {@link AirportDto }
     */
    public AirportDto convertToDto(Airport airport) {
        return modelMapper.map(airport, AirportDto.class);
    }

    /**
     * Convert {@link AirportDto} entity to {@link Airport }
     *
     * @param airportDto
     * @return {@link Airport }
     */
    public Airport convertToEntity(AirportDto airportDto) {
        return modelMapper.map(airportDto, Airport.class);
    }

    /**
     * Merge {@link AirportDto} entity to {@link Airport }
     *
     * @param airportDto
     * @return {@link Airport }
     */
    public void mergeToEntity(AirportDto airportDto, Airport airport) {
        modelMapper.map(airportDto, airport);
    }
}
