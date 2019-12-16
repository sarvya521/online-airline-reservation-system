package com.oars.service.impl;

import com.oars.constant.BookingStatus;
import com.oars.constant.SearchFlightConstants;
import com.oars.dao.AircraftRepository;
import com.oars.dao.AirportRepository;
import com.oars.dao.BookingRepository;
import com.oars.dao.FlightRepository;
import com.oars.dao.UserRepository;
import com.oars.dto.ActiveFlightDto;
import com.oars.dto.AircraftDto;
import com.oars.dto.AirlineRevenueDto;
import com.oars.dto.AirportDto;
import com.oars.dto.BookingDto;
import com.oars.dto.CustomerRevenueDto;
import com.oars.dto.FlightDto;
import com.oars.dto.FlightRevenueDto;
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
import com.oars.util.BookingUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
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
        boolean isSeatAvailableInFlight =
                isSeatAvailableInFlight(flight, bookingDto.getSeatClass());
        if (!isSeatAvailableInFlight) {
            booking.setStatus(BookingStatus.WAITING.name());
        } else {
            booking.setStatus(BookingStatus.CONFIRMED.name());
        }
        booking.setFlight(flight);
        User user = userRepository.findById(bookingDto.getUser().getId()).get();
        booking.setUser(user);
        booking.setPnr(BookingUtil.randomAlphaNumeric(10));
        booking.setBookingDate(LocalDate.now());
        booking.setAirline(flight.getAirline());
        if (Objects.equals(booking.getSeatClass(),
                SearchFlightConstants.SeatPreference.BUSINESS.name())) {
            if (isSeatAvailableInFlight) {
                flight.setRemainingBusinessSeats(flight.getRemainingBusinessSeats() - 1); // decrease remaining seats
            }
            booking.setCost(flight.getBusinessClassFare());
        } else if (Objects.equals(booking.getSeatClass(),
                SearchFlightConstants.SeatPreference.FIRST.name())) {
            if (isSeatAvailableInFlight) {
                flight.setRemainingFirstclassSeats(flight.getRemainingFirstclassSeats() - 1); // decrease remaining
                // seats
            }
            booking.setCost(flight.getFirstclassFare());
        } else if (Objects.equals(booking.getSeatClass(),
                SearchFlightConstants.SeatPreference.ECONOMY.name())) {
            if (isSeatAvailableInFlight) {
                flight.setRemainingEconomySeats(flight.getRemainingEconomySeats() - 1); // decrease remaining seats
            }
            booking.setCost(flight.getEconomyClassFare());
        }
        Booking bookingPersisted = transactionTemplate.execute(new TransactionCallback<Booking>() {
            @Override
            public Booking doInTransaction(TransactionStatus status) {
                return bookingRepository.saveAndFlush(booking);
            }
        });
        return prepareBookingDto(bookingPersisted);
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
        Booking bookingPersisted = transactionTemplate.execute(new TransactionCallback<Booking>() {
            @Override
            public Booking doInTransaction(TransactionStatus status) {
                return bookingRepository.saveAndFlush(booking);
            }
        });
        return prepareBookingDto(bookingPersisted);
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingId, Long userId, List<String> errors) {
        Optional<Booking> bookingOptional = bookingRepository.findById(bookingId);
        if (!bookingOptional.isPresent()) {
            log.error("booking {} not found", bookingId);
            errors.add("Booking not found");
            return;
        }
        Booking booking = bookingOptional.get();
        if (booking.getTravelDate().compareTo(LocalDate.now()) < 0) {
            log.error("Invalid cancel booking request. Booking traveling date is older");
            errors.add("Invalid cancel booking request. Booking traveling date is older");
            return;
        }
        if (!Objects.equals(userId, booking.getUser().getId())) {
            log.error("Invalid cancel booking request. Booking {} does not belong to user {}", bookingId, userId);
            errors.add("This is not your booking. Try to login with the user which was used while booking this " +
                    "reservation");
            return;
        }
        Flight flight = booking.getFlight();
        if (Objects.equals(BookingStatus.CONFIRMED.name(), booking.getStatus())
                &&
                Objects.equals(booking.getSeatClass(),
                        SearchFlightConstants.SeatPreference.BUSINESS.name())) {
            flight.setRemainingBusinessSeats(flight.getRemainingBusinessSeats() + 1); // increment remaining seats
        } else if (Objects.equals(BookingStatus.CONFIRMED.name(), booking.getStatus())
                &&
                Objects.equals(booking.getSeatClass(),
                        SearchFlightConstants.SeatPreference.FIRST.name())) {
            flight.setRemainingFirstclassSeats(flight.getRemainingFirstclassSeats() + 1); // increment remaining seats
        } else {
            log.error("Invalid cancel booking request. It is not BUSINESS or FIRST CLASS Booking");
            errors.add("Invalid cancel booking request. It is not BUSINESS or FIRST CLASS Booking");
            return;
        }
        booking.setStatus(BookingStatus.CANCELLED.name());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                bookingRepository.save(booking);
            }
        });
    }

    @Override
    public List<BookingDto> findAllByUser(Long userId) {
        User user = userRepository.findById(userId).get();
        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .sorted(Comparator.comparing(BookingDto::getTravelDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllPastBookingsForUser(Long userId) {
        User user = userRepository.findById(userId).get();
        List<Booking> bookings = bookingRepository.findByUserAndTravelDateLessThan(user, LocalDate.now());
        return bookings.stream()
                .map(this::prepareBookingDto)
                .sorted(Comparator.comparing(BookingDto::getTravelDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllUpcomingBookingsForUser(Long userId) {
        User user = userRepository.findById(userId).get();
        List<Booking> bookings = bookingRepository.findByUserAndTravelDateGreaterThanEqual(user, LocalDate.now());
        return bookings.stream()
                .map(this::prepareBookingDto)
                .sorted(Comparator.comparing(BookingDto::getTravelDate).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByBookingDateBetween(LocalDate from, LocalDate to) {
        List<Booking> bookings = bookingRepository.findByBookingDateBetween(from, to);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .sorted(Comparator.comparing(BookingDto::getBookingDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByFlight(FlightDto flightDto) {
        Flight flight = flightRepository.findById(flightDto.getId()).get();
        List<Booking> bookings = bookingRepository.findByFlight(flight);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByFlightId(Long flightId) {
        Flight flight = flightRepository.findById(flightId).get();
        List<Booking> bookings = bookingRepository.findByFlight(flight);
        return bookings.stream()
                .map(this::prepareBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByAirline(String airline) {
        List<Booking> bookings = bookingRepository.findByAirline(airline);
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
    public List<CustomerRevenueDto> findCustomerSortedByHighestRevenue() {
        List<BookingRepository.CustomerRevenue> customerRevenues =
                bookingRepository.findCustomersWithTheirTotalRevenue();

        return customerRevenues.stream()
                .map(customerRevenue -> {
                    CustomerRevenueDto customerRevenueDto =
                            userMapper.convertToCustomerRevenueDto(customerRevenue.getUser());
                    customerRevenueDto.setTotalRevenue(customerRevenue.getTotalRevenue());
                    return customerRevenueDto;
                })
                .sorted(Comparator.comparing(CustomerRevenueDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<AirlineRevenueDto> findAirlineSortedByHighestRevenue() {
        List<BookingRepository.AirlineRevenue> airlineRevenues =
                bookingRepository.findAirlinesWithTheirTotalRevenue();

        return airlineRevenues.stream()
                .map(airlineRevenue -> {
                    AirlineRevenueDto airlineRevenueDto = new AirlineRevenueDto();
                    airlineRevenueDto.setName(airlineRevenue.getAirline());
                    airlineRevenueDto.setTotalRevenue(airlineRevenue.getTotalRevenue());
                    return airlineRevenueDto;
                })
                .sorted(Comparator.comparing(AirlineRevenueDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightRevenueDto> findFlightsSortedByHighestRevenue() {
        List<BookingRepository.FlightRevenue> flightRevenues =
                bookingRepository.findFlightsWithTheirTotalRevenue();

        return flightRevenues.stream()
                .map(flightRevenue -> {
                    FlightRevenueDto flightRevenueDto =
                            flightMapper.convertToFlightRevenueDto(flightRevenue.getFlight());
                    flightRevenueDto.setTotalRevenue(flightRevenue.getTotalRevenue());
                    return flightRevenueDto;
                })
                .sorted(Comparator.comparing(FlightRevenueDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
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

    @Override
    public List<ActiveFlightDto> findFlightsSortedByHighestBookings() {
        List<BookingRepository.FlightBooking> flightsWithTotalBookings =
                bookingRepository.findFlightsWithTotalBookings();

        return flightsWithTotalBookings.stream()
                .map(flightBooking -> {
                    ActiveFlightDto activeFlightDto =
                            flightMapper.convertToActiveFlightDto(flightBooking.getFlight());
                    activeFlightDto.setTotalBookings(flightBooking.getTotalBookings());
                    return activeFlightDto;
                })
                .sorted(Comparator.comparing(ActiveFlightDto::getTotalBookings).reversed())
                .collect(Collectors.toList());

    }

    private boolean isSeatAvailableInFlight(Flight flight, String seatPreference) {
        SearchFlightConstants.SeatPreference seatClass = SearchFlightConstants.SeatPreference.valueOf(seatPreference);
        switch (seatClass) {
            case ECONOMY:
                return flight.getRemainingEconomySeats() > 0;
            case BUSINESS:
                return flight.getRemainingBusinessSeats() > 0;
            case FIRST:
                return flight.getRemainingFirstclassSeats() > 0;
            default:
                return false;
        }
    }
}
