package com.oars.service.impl;

import com.oars.dao.AircraftRepository;
import com.oars.dto.AircraftDto;
import com.oars.entity.Aircraft;
import com.oars.modelmapper.AircraftMapper;
import com.oars.service.AircraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AircraftServiceImpl implements AircraftService {

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private AircraftMapper aircraftMapper;

    private final TransactionTemplate transactionTemplate;

    public AircraftServiceImpl(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    @Transactional
    public boolean checkIfAircraftExists(String code) {
        return aircraftRepository.existsByCode(code);
    }

    @Override
    @Transactional
    public AircraftDto createAircraft(AircraftDto aircraftDto) {
        Aircraft aircraft = aircraftMapper.convertToEntity(aircraftDto);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Aircraft aircraftPersisted = aircraftRepository.saveAndFlush(aircraft);
                aircraftDto.setId(aircraftPersisted.getId());
            }
        });
        return aircraftDto;
    }

    @Override
    @Transactional
    public AircraftDto updateAircraft(AircraftDto aircraftDto) {
        Long id = aircraftDto.getId();
        Aircraft aircraft = aircraftRepository.findById(id).get();
        aircraftMapper.mergeToEntity(aircraftDto, aircraft);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                aircraftRepository.saveAndFlush(aircraft);
            }
        });
        return aircraftDto;
    }

    @Override
    @Transactional
    public void deleteAircraft(Long id) {
        Aircraft aircraft = aircraftRepository.findById(id).get();
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                aircraftRepository.delete(aircraft);
            }
        });
    }
}
