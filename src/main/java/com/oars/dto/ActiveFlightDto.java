package com.oars.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class ActiveFlightDto {
    private Long id;
    private LocalDate travelDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private AirportDto departureFrom;
    private AirportDto arrivalAt;
    private String airline;
    private Long totalBookings;
}
