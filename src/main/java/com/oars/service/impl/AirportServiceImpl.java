package com.oars.service.impl;

import com.oars.dao.AirportRepository;
import com.oars.dto.AirportDto;
import com.oars.entity.Airport;
import com.oars.modelmapper.AirportMapper;
import com.oars.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportServiceImpl implements AirportService {

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportMapper airportMapper;

    private final TransactionTemplate transactionTemplate;

    public AirportServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    @Transactional
    public List<AirportDto> getAllAirport() {
        List<Airport> airports = airportRepository.findAll();
        return airports.stream()
                .map(airport -> airportMapper.convertToDto(airport))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean checkIfAirportExists(String name, String alias) {
        return airportRepository.existsByName(name) || airportRepository.existsByAlias(alias);
    }

    @Override
    @Transactional
    public AirportDto createAirport(AirportDto airportDto) {
        Airport airport = airportMapper.convertToEntity(airportDto);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Airport airportPersisted = airportRepository.saveAndFlush(airport);
                airportDto.setId(airportPersisted.getId());
            }
        });
        return airportDto;
    }

    @Override
    @Transactional
    public AirportDto updateAirport(AirportDto airportDto) {
        Long id = airportDto.getId();
        Airport airport = airportRepository.findById(id).get();
        airportMapper.mergeToEntity(airportDto, airport);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                airportRepository.saveAndFlush(airport);
            }
        });
        return airportDto;
    }

    @Override
    @Transactional
    public void deleteAirport(Long id) {
        Airport airport = airportRepository.findById(id).get();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                airportRepository.delete(airport);
            }
        });
    }
}
