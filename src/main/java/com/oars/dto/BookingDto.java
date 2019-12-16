package com.oars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private String pnr;
    private LocalDate bookingDate;
    private LocalDate travelDate;
    private String seatClass;
    private Integer cost;
    private String airline;
    private String status;
    private FlightDto flight;
    private UserDto user;
}
