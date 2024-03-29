package com.oars.modelmapper;

import com.oars.dto.ActiveFlightDto;
import com.oars.dto.FlightDto;
import com.oars.dto.FlightRevenueDto;
import com.oars.entity.Flight;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class FlightMapper {

    @Autowired
    private ModelMapper modelMapper;

    @PostConstruct
    public void addMappings() {
        modelMapper.createTypeMap(Flight.class, FlightDto.class).addMappings(new PropertyMap<Flight,
                FlightDto>() {
            @Override
            protected void configure() {
                skip(destination.getDepartureFrom());
                skip(destination.getArrivalAt());
                skip(destination.getAircraft());
            }
        });

        modelMapper.createTypeMap(FlightDto.class, Flight.class).addMappings(new PropertyMap<FlightDto,
                Flight>() {
            @Override
            protected void configure() {
                skip(destination.getRemainingFirstclassSeats());
                skip(destination.getRemainingBusinessSeats());
                skip(destination.getRemainingEconomySeats());
                skip(destination.getDepartureFrom());
                skip(destination.getArrivalAt());
                skip(destination.getAircraft());
            }
        });

    }

    /**
     * Convert {@link Flight} entity to {@link FlightDto }
     *
     * @param flight
     * @return {@link FlightDto }
     */
    public FlightDto convertToDto(Flight flight) {
        return modelMapper.map(flight, FlightDto.class);
    }

    public ActiveFlightDto convertToActiveFlightDto(Flight flight) {
        return modelMapper.map(flight, ActiveFlightDto.class);
    }

    public FlightRevenueDto convertToFlightRevenueDto(Flight flight) {
        return modelMapper.map(flight, FlightRevenueDto.class);
    }

    /**
     * Convert {@link FlightDto} entity to {@link Flight }
     *
     * @param flightDto
     * @return {@link Flight }
     */
    public Flight convertToEntity(FlightDto flightDto) {
        return modelMapper.map(flightDto, Flight.class);
    }

    /**
     * Merge {@link FlightDto} entity to {@link Flight }
     *
     * @param flightDto
     * @return {@link Flight }
     */
    public void mergeToEntity(FlightDto flightDto, Flight flight) {
        flight.setTravelDate(flightDto.getTravelDate());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setFirstclassFare(flightDto.getFirstclassFare());
        flight.setBusinessClassFare(flightDto.getBusinessClassFare());
        flight.setEconomyClassFare(flightDto.getEconomyClassFare());
        flight.setAirline(flightDto.getAirline());
    }


}
