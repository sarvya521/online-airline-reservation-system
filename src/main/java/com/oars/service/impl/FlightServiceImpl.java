package com.oars.service.impl;

import com.oars.constant.SearchFlightConstants;
import com.oars.dao.AircraftRepository;
import com.oars.dao.AirportRepository;
import com.oars.dao.FlightRepository;
import com.oars.dto.AircraftDto;
import com.oars.dto.AirportDto;
import com.oars.dto.FlightDto;
import com.oars.dto.SearchFlightDto;
import com.oars.entity.Aircraft;
import com.oars.entity.Airport;
import com.oars.entity.Flight;
import com.oars.modelmapper.AircraftMapper;
import com.oars.modelmapper.AirportMapper;
import com.oars.modelmapper.FlightMapper;
import com.oars.service.FlightService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class FlightServiceImpl implements FlightService {

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

    private final TransactionTemplate transactionTemplate;

    public FlightServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public List<FlightDto> getAllFlights() {
        List<Flight> flights = flightRepository.findAll();
        return flights.stream().map(flight -> flightMapper.convertToDto(flight)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FlightDto createFlight(FlightDto flightDto) {
        Flight flight = flightMapper.convertToEntity(flightDto);
        Aircraft aircraft = aircraftRepository.findById(flight.getAircraft().getId()).get();
        flight.setAircraft(aircraft);
        Airport departureFrom = airportRepository.findById(flight.getDepartureFrom().getId()).get();
        flight.setDepartureFrom(departureFrom);
        Airport arrivalAt = airportRepository.findById(flight.getArrivalAt().getId()).get();
        flight.setArrivalAt(arrivalAt);
        flight.setRemainingEconomySeats(aircraft.getTotalEconomySeats());
        flight.setRemainingBusinessSeats(aircraft.getTotalBusinessSeats());
        flight.setRemainingFirstclassSeats(aircraft.getTotalFirstclassSeats());
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Flight flightPersisted = flightRepository.saveAndFlush(flight);
                flightDto.setId(flightPersisted.getId());
            }
        });
        return flightDto;
    }

    @Override
    @Transactional
    public FlightDto updateFlight(FlightDto flightDto) {
        Long id = flightDto.getId();
        Flight flight = flightRepository.findById(id).get();
        flightMapper.mergeToEntity(flightDto, flight);
        Aircraft aircraft = aircraftRepository.findById(flight.getAircraft().getId()).get();
        flight.setAircraft(aircraft);
        Airport departureFrom = airportRepository.findById(flight.getDepartureFrom().getId()).get();
        flight.setDepartureFrom(departureFrom);
        Airport arrivalAt = airportRepository.findById(flight.getArrivalAt().getId()).get();
        flight.setArrivalAt(arrivalAt);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                flightRepository.saveAndFlush(flight);
            }
        });
        return flightDto;
    }

    @Override
    @Transactional
    public void deleteFlight(Long id) {
        Flight flight = flightRepository.findById(id).get();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                flightRepository.delete(flight);
            }
        });
    }

    @Override
    public List<FlightDto> search(SearchFlightDto searchFlightDto, boolean forReturningJourney) {
        List<FlightDto> flights = null;
        if (!forReturningJourney) {
            flights =
                    findAllByTravelDateAndDepartureFromAndArrivalAt(
                            searchFlightDto.getTravelDate(),
                            searchFlightDto.getSourceAirportId(),
                            searchFlightDto.getDestinationAirportId()
                    );
        } else {
            flights =
                    findAllByTravelDateAndDepartureFromAndArrivalAt(
                            searchFlightDto.getReturnDate(),
                            searchFlightDto.getDestinationAirportId(),
                            searchFlightDto.getSourceAirportId()
                    );
        }
        flights = filterFlights(flights, searchFlightDto);
        flights = sortFlights(flights, searchFlightDto);
        return flights;
    }


    @Override
    @Transactional
    public List<FlightDto> findAllByTravelDateAndDepartureFromAndArrivalAt(LocalDate travelDate,
                                                                           Long departureAirportId,
                                                                           Long arrivalAirportId) {
        Airport departureAirport = airportRepository.findById(departureAirportId).get();
        Airport arrivalAirport = airportRepository.findById(arrivalAirportId).get();
        List<Flight> flights =
                flightRepository.findByTravelDateAndDepartureFromAndArrivalAt(
                        travelDate, departureAirport, arrivalAirport);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByBusinessClassFareBetween(int from, int to) {
        List<Flight> flights = flightRepository.findByBusinessClassFareBetween(from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByFirstclassFareBetween(int from, int to) {
        List<Flight> flights = flightRepository.findByFirstclassFareBetween(from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByEconomyClassFareBetween(int from, int to) {
        List<Flight> flights = flightRepository.findByEconomyClassFareBetween(from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByAirline(String airline) {
        List<Flight> flights = flightRepository.findByAirline(airline);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByAirlineOrBusinessClassFareBetween(String airline, int from, int to) {
        List<Flight> flights = flightRepository.findByAirlineOrBusinessClassFareBetween(airline, from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDto> findAllByAirlineOrFirstclassFareBetween(String airline, int from, int to) {
        List<Flight> flights = flightRepository.findByAirlineOrFirstclassFareBetween(airline, from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDto> findAllByAirlineOrEconomyClassFareBetween(String airline, int from, int to) {
        List<Flight> flights = flightRepository.findByAirlineOrEconomyClassFareBetween(airline, from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByDepartureFromOrArrivalAt(AirportDto departureFrom, AirportDto arrivalAt) {
        Airport departure = airportRepository.findById(departureFrom.getId()).get();
        Airport arrival = airportRepository.findById(arrivalAt.getId()).get();
        List<Flight> flights = flightRepository.findByDepartureFromOrArrivalAt(departure, arrival);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAirlines(List<FlightDto> flights) {
        return flights.stream()
                .map(FlightDto::getAirline)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getSortedFares(List<FlightDto> flights, SearchFlightDto searchFlightDto) {
        return flights.stream()
                .map(flightDto -> {
                    switch (searchFlightDto.getSeatPreference()) {
                        case FIRST:
                            return flightDto.getFirstclassFare();
                        case BUSINESS:
                            return flightDto.getBusinessClassFare();
                        default:
                            return flightDto.getEconomyClassFare();
                    }
                }).sorted()
                .collect(Collectors.toList());
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

    private List<FlightDto> sortFlights(List<FlightDto> flights, SearchFlightDto searchFlightDto) {
        if (Objects.isNull(searchFlightDto.getSortBy())) {
            searchFlightDto.setSortBy(SearchFlightConstants.SortBy.PRICE_ASC); // default sort
        }
        SearchFlightConstants.SortBy sortBy = searchFlightDto.getSortBy();
        switch (sortBy) {
            case PRICE_ASC:
                return flights.stream()
                        .sorted((f1, f2) -> {
                            switch (searchFlightDto.getSeatPreference()) {
                                case FIRST:
                                    return f1.getFirstclassFare().compareTo(f2.getFirstclassFare());
                                case BUSINESS:
                                    return f1.getBusinessClassFare().compareTo(f2.getBusinessClassFare());
                                default:
                                    return f1.getEconomyClassFare().compareTo(f2.getEconomyClassFare());
                            }
                        })
                        .collect(Collectors.toList());
            case PRICE_DESC:
                return flights.stream()
                        .sorted((f1, f2) -> {
                            switch (searchFlightDto.getSeatPreference()) {
                                case FIRST:
                                    return f2.getFirstclassFare().compareTo(f1.getFirstclassFare());
                                case BUSINESS:
                                    return f2.getBusinessClassFare().compareTo(f1.getBusinessClassFare());
                                default:
                                    return f2.getEconomyClassFare().compareTo(f1.getEconomyClassFare());
                            }
                        })
                        .collect(Collectors.toList());
            case DEPARTURE_ASC:
                return flights.stream()
                        .sorted(Comparator.comparing(FlightDto::getDepartureTime))
                        .collect(Collectors.toList());
            case DEPARTURE_DESC:
                return flights.stream()
                        .sorted(Comparator.comparing(FlightDto::getDepartureTime).reversed())
                        .collect(Collectors.toList());
            case ARRIVAL_ASC:
                return flights.stream()
                        .sorted(Comparator.comparing(FlightDto::getArrivalTime))
                        .collect(Collectors.toList());
            case ARRIVAL_DESC:
                return flights.stream()
                        .sorted(Comparator.comparing(FlightDto::getArrivalTime).reversed())
                        .collect(Collectors.toList());
            default:
                log.error("Unexpected Error");
                break;
        }
        return flights;
    }

    private List<FlightDto> filterFlights(List<FlightDto> flights, SearchFlightDto searchFlightDto) {
        List<FlightDto> filteredFlights = flights;
        if (Objects.nonNull(searchFlightDto.getSeatPreference())) {
            filteredFlights =
                    filteredFlights.stream()
                            .filter(flightDto -> {
                                switch (searchFlightDto.getSeatPreference()) {
                                    case FIRST:
                                        return flightDto.getRemainingFirstclassSeats() > 0;
                                    case BUSINESS:
                                        return flightDto.getRemainingBusinessSeats() > 0;
                                    default:
                                        return flightDto.getRemainingEconomySeats() > 0;
                                }
                            })
                            .collect(Collectors.toList());
        }
        if (Objects.nonNull(searchFlightDto.getMinPrice())) {
            filteredFlights =
                    filteredFlights.stream()
                            .filter(flightDto -> {
                                switch (searchFlightDto.getSeatPreference()) {
                                    case FIRST:
                                        return flightDto.getFirstclassFare() >= searchFlightDto.getMinPrice();
                                    case BUSINESS:
                                        return flightDto.getBusinessClassFare() >= searchFlightDto.getMinPrice();
                                    default:
                                        return flightDto.getEconomyClassFare() >= searchFlightDto.getMinPrice();
                                }
                            })
                            .collect(Collectors.toList());
        }
        if (Objects.nonNull(searchFlightDto.getMaxPrice())) {
            filteredFlights =
                    filteredFlights.stream()
                            .filter(flightDto -> {
                                switch (searchFlightDto.getSeatPreference()) {
                                    case FIRST:
                                        return flightDto.getFirstclassFare() <= searchFlightDto.getMaxPrice();
                                    case BUSINESS:
                                        return flightDto.getBusinessClassFare() <= searchFlightDto.getMaxPrice();
                                    default:
                                        return flightDto.getEconomyClassFare() <= searchFlightDto.getMaxPrice();
                                }
                            })
                            .collect(Collectors.toList());
        }
        if (Objects.nonNull(searchFlightDto.getAirlines()) && !searchFlightDto.getAirlines().isEmpty()) {
            filteredFlights =
                    filteredFlights.stream()
                            .filter(flightDto -> searchFlightDto.getAirlines().contains(flightDto.getAirline()))
                            .collect(Collectors.toList());

        }
        return filteredFlights;
    }
}
