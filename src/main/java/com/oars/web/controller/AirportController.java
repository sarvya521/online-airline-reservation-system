package com.oars.web.controller;

import com.oars.constant.Role;
import com.oars.dto.AirportDto;
import com.oars.service.AirportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@Log4j2
@Controller
@RequestMapping("/airport")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @GetMapping("/crud")
    public ModelAndView crudAirport(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        ModelAndView mav = new ModelAndView("airport");
        List<AirportDto> airports = airportService.getAllAirport();
        mav.addObject("airports", airports);
        return mav;
    }

    @PostMapping("/addedit")
    public ModelAndView addEditAirport(HttpServletRequest request, HttpServletResponse res) {
        String loggedInUser = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), loggedInUser)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        String name = request.getParameter("name");
        String alias = request.getParameter("alias");

        AirportDto airportDto = new AirportDto();
        airportDto.setName(name);
        airportDto.setAlias(alias);

        String airportIdStr = request.getParameter("airportId");
        if (StringUtils.isEmpty(airportIdStr)) {
            airportService.createAirport(airportDto);
        } else {
            airportDto.setId(Long.parseLong(airportIdStr));
            airportService.updateAirport(airportDto);
        }
        return crudAirport(request, res);
    }

    @PostMapping("/delete")
    public ModelAndView deleteAirport(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        Long airportId = Long.parseLong(request.getParameter("airport"));
        airportService.deleteAirport(airportId);
        return crudAirport(request, res);
    }

    @PostMapping
    public ModelAndView createAirport(HttpServletRequest request, HttpServletResponse res) {
        String name = request.getParameter("name");
        String alias = request.getParameter("alias");
        AirportDto airportDto = new AirportDto();
        airportDto.setName(name);
        airportDto.setAlias(alias);

        boolean ifAirportExists = airportService.checkIfAirportExists(name, alias);
        if (ifAirportExists) {
            String message = "airport with" + name + " or " + alias + " already exists";
            return new ModelAndView("unsuccessfulRegistration", "message", message);
        } else {
            airportService.createAirport(airportDto);
            log.info("airport created successfully");
            return new ModelAndView("homepage", "message", "airport created successfully!");
        }
    }

    @PutMapping
    public ModelAndView updateAirport(HttpServletRequest request, HttpServletResponse res) {
        Long airportId = Long.parseLong(request.getParameter("airportId"));
        String name = request.getParameter("name");
        String alias = request.getParameter("alias");
        AirportDto airportDto = new AirportDto();
        airportDto.setId(airportId);
        airportDto.setName(name);
        airportDto.setAlias(alias);

        airportService.updateAirport(airportDto);
        log.info("airport updated successfully");
        return new ModelAndView("homepage", "message", "airport updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteAirport(@PathVariable(value = "id") Long airportId) {
        airportService.deleteAirport(airportId);
        return new ModelAndView("homepage", "message", "airport deleted successfully!");
    }
}