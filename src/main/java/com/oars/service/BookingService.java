package com.oars.service;

import com.oars.dto.ActiveFlightDto;
import com.oars.dto.AirlineRevenueDto;
import com.oars.dto.BookingDto;
import com.oars.dto.CustomerRevenueDto;
import com.oars.dto.FlightDto;
import com.oars.dto.FlightRevenueDto;
import com.oars.dto.UserDto;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingDto bookingDto);

    BookingDto updateBooking(BookingDto bookingDto);

    void deleteBooking(Long bookingId, Long id, List<String> errors);

    List<BookingDto> findAllByUser(Long userId);

    List<BookingDto> getAllPastBookingsForUser(Long userId);

    List<BookingDto> getAllUpcomingBookingsForUser(Long userId);

    List<BookingDto> findAllByBookingDateBetween(LocalDate from, LocalDate to);

    List<BookingDto> findAllByFlight(FlightDto flightDto);

    List<BookingDto> findAllByFlightId(Long flightId);

    List<BookingDto> findAllByAirline(String airline);

    UserDto findCustomerWithTheMostTotalRevenue();

    FlightDto findFlightWithHighestBookings();

    List<ActiveFlightDto> findFlightsSortedByHighestBookings();

    List<CustomerRevenueDto> findCustomerSortedByHighestRevenue();

    List<AirlineRevenueDto> findAirlineSortedByHighestRevenue();

    List<FlightRevenueDto> findFlightsSortedByHighestRevenue();

    List<BookingDto> getFlightWaitingList(Long flightId);
}
