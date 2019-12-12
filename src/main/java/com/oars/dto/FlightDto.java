package com.oars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.util.Date;


@Data
@NoArgsConstructor
public class FlightDto {
    private Long id;
    private Date travelDate;
    private Time departureTime;
    private Time arrivalTime;
    private AirportDto departureFrom;
    private AirportDto arrivalAt;
    private Integer businessClassFare;
    private Integer firstclassFare;
    private Integer economyClassFare;
    private Integer remainingBusinessSeats;
    private Integer remainingFirstclassSeats;
    private Integer remainingEconomySeats;
    private String airline;
    private AircraftDto aircraft;
}
