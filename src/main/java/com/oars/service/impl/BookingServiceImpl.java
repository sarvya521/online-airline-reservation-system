package com.oars.service.impl;

import com.oars.dao.AircraftRepository;
import com.oars.dao.AirportRepository;
import com.oars.dao.BookingRepository;
import com.oars.dao.FlightRepository;
import com.oars.dao.UserRepository;
import com.oars.dto.AircraftDto;
import com.oars.dto.AirportDto;
import com.oars.dto.BookingDto;
import com.oars.dto.FlightDto;
import com.oars.dto.UserDto;
import com.oars.entity.Booking;
import com.oars.entity.Flight;
import com.oars.entity.User;
import com.oars.modelmapper.AircraftMapper;
import com.oars.modelmapper.AirportMapper;
import com.oars.modelmapper.BookingMapper;
import com.oars.modelmapper.FlightMapper;
import com.oars.modelmapper.UserMapper;
import com.oars.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private FlightMapper flightMapper;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private AircraftMapper aircraftMapper;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportMapper airportMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    private final TransactionTemplate transactionTemplate;

    public BookingServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    private BookingDto prepareBookingDto(Booking booking) {
        BookingDto bookingDto = bookingMapper.convertToDto(booking);

        FlightDto flightDto = prepareFlightDto(booking.getFlight());
        bookingDto.setFlight(flightDto);

        UserDto userDto = userMapper.convertToDto(booking.getUser());
        bookingDto.setUser(userDto);

        return bookingDto;
    }

    private FlightDto prepareFlightDto(Flight flight) {
        FlightDto flightDto = flightMapper.convertToDto(flight);
        AirportDto deptFrom = airportMapper.convertToDto(flight.getDepartureFrom());
        flightDto.setDepartureFrom(deptFrom);
        AirportDto arrivalAt = airportMapper.convertToDto(flight.getArrivalAt());
        flightDto.setArrivalAt(arrivalAt);
        AircraftDto aircraft = aircraftMapper.convertToDto(flight.getAircraft());
        flightDto.setAircraft(aircraft);
        return flightDto;
    }

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto) {
        Booking booking = bookingMapper.convertToEntity(bookingDto);
        Flight flight = flightRepository.findById(bookingDto.getFlight().getId()).get();
        booking.setFlight(flight);
        User user = userRepository.findById(bookingDto.getUser().getId()).get();
        booking.setUser(user);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Booking bookingPersisted = bookingRepository.saveAndFlush(booking);
                bookingDto.setId(bookingPersisted.getId());
            }
        });
        return bookingDto;
    }

    @Override
    @Transactional
    public BookingDto updateBooking(BookingDto bookingDto) {
        Long id = bookingDto.getId();
        Booking booking = bookingRepository.findById(id).get();
        bookingMapper.mergeToEntity(bookingDto, booking);
        Flight flight = flightRepository.findById(bookingDto.getFlight().getId()).get();
        booking.setFlight(flight);
        User user = userRepository.findById(bookingDto.getUser().getId()).get();
        booking.setUser(user);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                bookingRepository.saveAndFlush(booking);
            }
        });
        return bookingDto;
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id).get();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                bookingRepository.delete(booking);
            }
        });
    }

    @Override
    public List<BookingDto> findAllByUser(UserDto userDto) {
        User user = userRepository.findById(userDto.getId()).get();
        List<Booking> bookings = bookingRepository.findAllByUser(user);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByBookingDateBetween(Date from, Date to) {
        List<Booking> bookings = bookingRepository.findAllByBookingDateBetween(from, to);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByFlight(FlightDto flightDto) {
        Flight flight = flightRepository.findById(flightDto.getId()).get();
        List<Booking> bookings = bookingRepository.findAllByFlight(flight);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByAirline(String airline) {
        List<Booking> bookings = bookingRepository.findAllByAirline(airline);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findCustomerWithTheMostTotalRevenue() {
        List<BookingRepository.CustomerRevenue> customerRevenues =
                bookingRepository.findCustomersWithTheirTotalRevenue();
        User user =
                customerRevenues.stream()
                        .sorted(Comparator.comparing(BookingRepository.CustomerRevenue::getTotalRevenue).reversed())
                        .findFirst().get()
                        .getUser();
        return userMapper.convertToDto(user);
    }

    @Override
    public FlightDto findFlightWithHighestBookings() {
        List<BookingRepository.FlightBooking> flightsWithTotalBookings =
                bookingRepository.findFlightsWithTotalBookings();
        Flight flight =
                flightsWithTotalBookings.stream()
                        .sorted(Comparator.comparing(BookingRepository.FlightBooking::getTotalBookings).reversed())
                        .findFirst().get()
                        .getFlight();
        return prepareFlightDto(flight);

    }
}
