package com.oars.service;

import com.oars.dto.BookingDto;
import com.oars.dto.FlightDto;
import com.oars.dto.UserDto;

import java.util.Date;
import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto);

    BookingDto updateBooking(BookingDto bookingDto);

    void deleteBooking(Long id);

    List<BookingDto> findAllByUser(UserDto userDto);

    List<BookingDto> findAllByBookingDateBetween(Date from, Date to);

    List<BookingDto> findAllByFlight(FlightDto flightDto);

    List<BookingDto> findAllByAirline(String airline);

    UserDto findCustomerWithTheMostTotalRevenue();

    FlightDto findFlightWithHighestBookings();
}
