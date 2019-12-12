package com.oars.service.impl;

import com.oars.dao.AircraftRepository;
import com.oars.dao.AirportRepository;
import com.oars.dao.FlightRepository;
import com.oars.dto.AircraftDto;
import com.oars.dto.AirportDto;
import com.oars.dto.FlightDto;
import com.oars.entity.Aircraft;
import com.oars.entity.Airport;
import com.oars.entity.Flight;
import com.oars.modelmapper.AircraftMapper;
import com.oars.modelmapper.AirportMapper;
import com.oars.modelmapper.FlightMapper;
import com.oars.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public FlightDto createFlight(FlightDto flightDto) {
        Flight flight = flightMapper.convertToEntity(flightDto);
        Aircraft aircraft = aircraftRepository.findById(flight.getAircraft().getId()).get();
        flight.setAircraft(aircraft);
        Airport departureFrom = airportRepository.findById(flight.getDepartureFrom().getId()).get();
        flight.setDepartureFrom(departureFrom);
        Airport arrivalAt = airportRepository.findById(flight.getArrivalAt().getId()).get();
        flight.setArrivalAt(arrivalAt);
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
    @Transactional
    public List<FlightDto> findAllByTravelDateAndDepartureFrom(Date travelDate, AirportDto departureFrom) {
        Airport departureAirport = airportRepository.findById(departureFrom.getId()).get();
        List<Flight> flights = flightRepository.findAllByTravelDateAndDepartureFrom(travelDate, departureAirport);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByBusinessClassFareBetween(int from, int to) {
        List<Flight> flights = flightRepository.findAllByBusinessClassFareBetween(from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByFirstclassFareBetween(int from, int to) {
        List<Flight> flights = flightRepository.findAllByFirstclassFareBetween(from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByEconomyClassFareBetween(int from, int to) {
        List<Flight> flights = flightRepository.findAllByEconomyClassFareBetween(from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByAirline(String airline) {
        List<Flight> flights = flightRepository.findAllByAirline(airline);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByAirlineOrBusinessClassFareBetween(String airline, int from, int to) {
        List<Flight> flights = flightRepository.findAllByAirlineOrBusinessClassFareBetween(airline, from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDto> findAllByAirlineOrFirstclassFareBetween(String airline, int from, int to) {
        List<Flight> flights = flightRepository.findAllByAirlineOrFirstclassFareBetween(airline, from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FlightDto> findAllByAirlineOrEconomyClassFareBetween(String airline, int from, int to) {
        List<Flight> flights = flightRepository.findAllByAirlineOrEconomyClassFareBetween(airline, from, to);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<FlightDto> findAllByDepartureFromOrArrivalAt(AirportDto departureFrom, AirportDto arrivalAt) {
        Airport departure = airportRepository.findById(departureFrom.getId()).get();
        Airport arrival = airportRepository.findById(arrivalAt.getId()).get();
        List<Flight> flights = flightRepository.findAllByDepartureFromOrArrivalAt(departure, arrival);
        return flights.stream()
                .map(this::prepareFlightDto)
                .collect(Collectors.toList());
    }
}
