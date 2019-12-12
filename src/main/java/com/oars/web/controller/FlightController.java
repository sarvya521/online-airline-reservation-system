package com.oars.web.controller;

import com.oars.dto.AircraftDto;
import com.oars.dto.AirportDto;
import com.oars.dto.FlightDto;
import com.oars.service.FlightService;
import com.oars.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Time;
import java.util.Date;

@Log4j2
@Controller
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @PostMapping
    public ModelAndView createFlight(HttpServletRequest request, HttpServletResponse res) {
        Date travelDate = DateUtil.parseDate(request.getParameter("travelDate"));
        Time departureTime = DateUtil.parseTime(request.getParameter("departureTime"));
        Time arrivalTime = DateUtil.parseTime(request.getParameter("arrivalTime"));
        AirportDto departureFrom = new AirportDto();
        departureFrom.setId(Long.parseLong(request.getParameter("name")));
        AirportDto arrivalAt = new AirportDto();
        arrivalAt.setId(Long.parseLong(request.getParameter("name")));
        String businessClassFareStr = request.getParameter("businessClassFare");
        Integer businessClassFare = StringUtils.isEmpty(businessClassFareStr) ? 0 :
                Integer.parseInt(businessClassFareStr);
        String firstclassFareStr = request.getParameter("firstclassFare");
        Integer firstclassFare = StringUtils.isEmpty(firstclassFareStr) ? 0 :
                Integer.parseInt(firstclassFareStr);
        String economyClassFareStr = request.getParameter("economyClassFare");
        Integer economyClassFare = StringUtils.isEmpty(economyClassFareStr) ? 0 :
                Integer.parseInt(economyClassFareStr);
        String airline = request.getParameter("airline");
        AircraftDto aircraft = new AircraftDto();
        aircraft.setId(Long.parseLong(request.getParameter("name")));

        FlightDto flightDto = new FlightDto();
        flightDto.setTravelDate(travelDate);
        flightDto.setDepartureTime(departureTime);
        flightDto.setArrivalTime(arrivalTime);
        flightDto.setDepartureFrom(departureFrom);
        flightDto.setArrivalAt(arrivalAt);
        flightDto.setBusinessClassFare(businessClassFare);
        flightDto.setFirstclassFare(firstclassFare);
        flightDto.setEconomyClassFare(economyClassFare);
        flightDto.setAirline(airline);
        flightDto.setAircraft(aircraft);

        flightService.createFlight(flightDto);
        log.info("flight created successfully");
        return new ModelAndView("homepage", "message", "flight created successfully!");
    }

    @PutMapping
    public ModelAndView updateFlight(HttpServletRequest request, HttpServletResponse res) {
        Long flightId = Long.parseLong(request.getParameter("flightId"));
        Date travelDate = DateUtil.parseDate(request.getParameter("travelDate"));
        Time departureTime = DateUtil.parseTime(request.getParameter("departureTime"));
        Time arrivalTime = DateUtil.parseTime(request.getParameter("arrivalTime"));
        AirportDto departureFrom = new AirportDto();
        departureFrom.setId(Long.parseLong(request.getParameter("name")));
        AirportDto arrivalAt = new AirportDto();
        arrivalAt.setId(Long.parseLong(request.getParameter("name")));
        String businessClassFareStr = request.getParameter("businessClassFare");
        Integer businessClassFare = StringUtils.isEmpty(businessClassFareStr) ? 0 :
                Integer.parseInt(businessClassFareStr);
        String firstclassFareStr = request.getParameter("firstclassFare");
        Integer firstclassFare = StringUtils.isEmpty(firstclassFareStr) ? 0 :
                Integer.parseInt(firstclassFareStr);
        String economyClassFareStr = request.getParameter("economyClassFare");
        Integer economyClassFare = StringUtils.isEmpty(economyClassFareStr) ? 0 :
                Integer.parseInt(economyClassFareStr);
        String airline = request.getParameter("airline");
        AircraftDto aircraft = new AircraftDto();
        aircraft.setId(Long.parseLong(request.getParameter("name")));

        FlightDto flightDto = new FlightDto();
        flightDto.setId(flightId);
        flightDto.setTravelDate(travelDate);
        flightDto.setDepartureTime(departureTime);
        flightDto.setArrivalTime(arrivalTime);
        flightDto.setDepartureFrom(departureFrom);
        flightDto.setArrivalAt(arrivalAt);
        flightDto.setBusinessClassFare(businessClassFare);
        flightDto.setFirstclassFare(firstclassFare);
        flightDto.setEconomyClassFare(economyClassFare);
        flightDto.setAirline(airline);
        flightDto.setAircraft(aircraft);

        flightService.updateFlight(flightDto);
        log.info("flight updated successfully");
        return new ModelAndView("homepage", "message", "flight updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteFlight(@PathVariable(value = "id") Long flightId) {
        flightService.deleteFlight(flightId);
        return new ModelAndView("homepage", "message", "flight deleted successfully!");
    }
}