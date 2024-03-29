package com.oars.web.controller;

import com.oars.constant.Role;
import com.oars.dto.AircraftDto;
import com.oars.service.AircraftService;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/aircraft")
public class AircraftController {

    @Autowired
    private AircraftService aircraftService;

    @GetMapping("/crud")
    public ModelAndView crudAircraft(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        ModelAndView mav = new ModelAndView("aircraft");
        List<AircraftDto> aircrafts = aircraftService.getAllAircraft();
        aircrafts =
                aircrafts.stream()
                        .sorted(Comparator.comparing(AircraftDto::getId).reversed())
                        .collect(Collectors.toList());
        mav.addObject("aircrafts", aircrafts);
        return mav;
    }

    @PostMapping("/addedit")
    public ModelAndView addEditAircraft(HttpServletRequest request, HttpServletResponse res) {
        String loggedInUser = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), loggedInUser)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        String code = request.getParameter("code");
        String model = request.getParameter("model");
        Integer totalBusinessSeats = Integer.parseInt(request.getParameter("totalBusinessSeats"));
        Integer totalFirstclassSeats = Integer.parseInt(request.getParameter("totalFirstclassSeats"));
        Integer totalEconomySeats = Integer.parseInt(request.getParameter("totalEconomySeats"));

        AircraftDto aircraftDto = new AircraftDto();
        aircraftDto.setCode(code);
        aircraftDto.setModel(model);
        aircraftDto.setTotalEconomySeats(totalEconomySeats);
        aircraftDto.setTotalBusinessSeats(totalBusinessSeats);
        aircraftDto.setTotalFirstclassSeats(totalFirstclassSeats);

        String aircraftIdStr = request.getParameter("aircraftId");
        if (StringUtils.isEmpty(aircraftIdStr)) {
            aircraftService.createAircraft(aircraftDto);
        } else {
            aircraftDto.setId(Long.parseLong(aircraftIdStr));
            aircraftService.updateAircraft(aircraftDto);
        }
        return crudAircraft(request, res);
    }

    @PostMapping("/delete")
    public ModelAndView deleteAircraft(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        Long aircraftId = Long.parseLong(request.getParameter("aircraft"));
        aircraftService.deleteAircraft(aircraftId);
        return crudAircraft(request, res);
    }

    @PostMapping
    public ModelAndView createAircraft(HttpServletRequest request, HttpServletResponse res) {
        String code = request.getParameter("code");
        String model = request.getParameter("model");
        String totalBusinessSeatsStr = request.getParameter("totalBusinessSeats");
        int totalBusinessSeats = StringUtils.isEmpty(totalBusinessSeatsStr) ? 0 :
                Integer.parseInt(totalBusinessSeatsStr);
        String totalFirstclassSeatsStr = request.getParameter("totalFirstclassSeats");
        int totalFirstclassSeats = StringUtils.isEmpty(totalFirstclassSeatsStr) ? 0 :
                Integer.parseInt(totalFirstclassSeatsStr);
        String totalEconomySeatsStr = request.getParameter("totalEconomySeats");
        int totalEconomySeats = StringUtils.isEmpty(totalEconomySeatsStr) ? 0 :
                Integer.parseInt(totalEconomySeatsStr);
        AircraftDto aircraftDto = new AircraftDto();
        aircraftDto.setCode(code);
        aircraftDto.setModel(model);
        aircraftDto.setTotalBusinessSeats(totalBusinessSeats);
        aircraftDto.setTotalFirstclassSeats(totalFirstclassSeats);
        aircraftDto.setTotalEconomySeats(totalEconomySeats);

        boolean ifAircraftExists = aircraftService.checkIfAircraftExists(code);
        if (ifAircraftExists) {
            String message = "aircraft " + code + " already exists";
            return new ModelAndView("unsuccessfulRegistration", "message", message);
        } else {
            aircraftService.createAircraft(aircraftDto);
            log.info("aircraft created successfully");
            return new ModelAndView("homepage", "message", "aircraft created successfully!");
        }
    }

    @PutMapping
    public ModelAndView updateAircraft(HttpServletRequest request, HttpServletResponse res) {
        Long aircraftId = Long.parseLong(request.getParameter("aircraftId"));
        String code = request.getParameter("code");
        String model = request.getParameter("model");
        String totalBusinessSeatsStr = request.getParameter("totalBusinessSeats");
        int totalBusinessSeats = StringUtils.isEmpty(totalBusinessSeatsStr) ? 0 :
                Integer.parseInt(totalBusinessSeatsStr);
        String totalFirstclassSeatsStr = request.getParameter("totalFirstclassSeats");
        int totalFirstclassSeats = StringUtils.isEmpty(totalFirstclassSeatsStr) ? 0 :
                Integer.parseInt(totalFirstclassSeatsStr);
        String totalEconomySeatsStr = request.getParameter("totalEconomySeats");
        int totalEconomySeats = StringUtils.isEmpty(totalEconomySeatsStr) ? 0 :
                Integer.parseInt(totalEconomySeatsStr);
        AircraftDto aircraftDto = new AircraftDto();
        aircraftDto.setId(aircraftId);
        aircraftDto.setCode(code);
        aircraftDto.setModel(model);
        aircraftDto.setTotalBusinessSeats(totalBusinessSeats);
        aircraftDto.setTotalFirstclassSeats(totalFirstclassSeats);
        aircraftDto.setTotalEconomySeats(totalEconomySeats);

        aircraftService.updateAircraft(aircraftDto);
        log.info("aircraft updated successfully");
        return new ModelAndView("homepage", "message", "aircraft updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteAircraft(@PathVariable(value = "id") Long aircraftId) {
        aircraftService.deleteAircraft(aircraftId);
        return new ModelAndView("homepage", "message", "aircraft deleted successfully!");
    }
}