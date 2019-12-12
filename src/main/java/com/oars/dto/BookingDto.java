package com.oars.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private String pnr;
    private Date bookingDate;
    private Date travelDate;
    private String seatClass;
    private Integer cost;
    private String airline;
    private FlightDto flight;
    private UserDto user;
}
