package com.oars.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class FlightDto {
    private Long id;
    private LocalDate travelDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
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
