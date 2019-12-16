package com.oars.web.controller;

import com.oars.constant.Role;
import com.oars.constant.SearchFlightConstants;
import com.oars.dto.AircraftDto;
import com.oars.dto.AirportDto;
import com.oars.dto.FlightDto;
import com.oars.dto.SearchFlightDto;
import com.oars.service.AircraftService;
import com.oars.service.AirportService;
import com.oars.service.FlightService;
import com.oars.util.DateUtil;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.oars.constant.SearchFlightConstants.ALLOWED_SEAT_PREFERENCES;

@Log4j2
@Controller
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @Autowired
    private AirportService airportService;

    @Autowired
    private AircraftService aircraftService;

    @GetMapping("/crud")
    public ModelAndView crudFlight(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        List<String> errors = new ArrayList<>();
        ModelAndView mav = new ModelAndView("flight");
        List<FlightDto> flights = flightService.getAllFlights();
        mav.addObject("flights", flights);
        List<AirportDto> airports = airportService.getAllAirport();
        if (airports.isEmpty()) {
            errors.add("no aiport found in the system to create flight");
        }
        List<AircraftDto> aircrafts = aircraftService.getAllAircraft();
        if (aircrafts.isEmpty()) {
            errors.add("no aircraft found in the system to create flight");
        }
        if (!errors.isEmpty()) {
            return new ModelAndView("error", "message", errors.toString());
        }
        mav.addObject("airports", airports);
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
        LocalDate travelDate = DateUtil.parseDate(request.getParameter("travelDate"));
        LocalTime departureTime = DateUtil.parseTime(request.getParameter("departureTime"));
        LocalTime arrivalTime = DateUtil.parseTime(request.getParameter("arrivalTime"));
        AirportDto departureFrom = new AirportDto();
        departureFrom.setId(Long.parseLong(request.getParameter("departureFrom")));
        AirportDto arrivalAt = new AirportDto();
        arrivalAt.setId(Long.parseLong(request.getParameter("arrivalAt")));
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
        aircraft.setId(Long.parseLong(request.getParameter("aircraft")));

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

        String flightIdStr = request.getParameter("flightId");
        if (StringUtils.isEmpty(flightIdStr)) {
            flightService.createFlight(flightDto);
        } else {
            flightDto.setId(Long.parseLong(flightIdStr));
            flightService.updateFlight(flightDto);
        }
        return crudFlight(request, res);
    }

    @PostMapping("/delete")
    public ModelAndView deleteFlight(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        Long flightId = Long.parseLong(request.getParameter("flight"));
        flightService.deleteFlight(flightId);
        return crudFlight(request, res);
    }

    @PostMapping
    public ModelAndView createFlight(HttpServletRequest request, HttpServletResponse res) {
        LocalDate travelDate = DateUtil.parseDate(request.getParameter("travelDate"));
        LocalTime departureTime = DateUtil.parseTime(request.getParameter("departureTime"));
        LocalTime arrivalTime = DateUtil.parseTime(request.getParameter("arrivalTime"));
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
        LocalDate travelDate = DateUtil.parseDate(request.getParameter("travelDate"));
        LocalTime departureTime = DateUtil.parseTime(request.getParameter("departureTime"));
        LocalTime arrivalTime = DateUtil.parseTime(request.getParameter("arrivalTime"));
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

    @RequestMapping(value = "/airport", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getFlightsForAirport(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("airportflights");
        List<String> errors = new ArrayList<>();
        List<AirportDto> airports = airportService.getAllAirport();
        if (airports.isEmpty()) {
            errors.add("No airports found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("airports", airports);
        String airportStr = request.getParameter("airport");
        if (Objects.isNull(airportStr)) {
            return mav;
        }
        Long airportId = Long.parseLong(request.getParameter("airport"));
        AirportDto airportDto = new AirportDto();
        airportDto.setId(airportId);
        List<FlightDto> flights = flightService.findAllByDepartureFromOrArrivalAt(airportDto, airportDto);
        if (flights.isEmpty()) {
            errors.add("No flights found for this airport");
            mav.addObject("errors", errors);
            return mav;
        }
        List<FlightDto> arrivingFlights =
                flights.stream()
                        .filter(flightDto -> flightDto.getArrivalAt().equals(airportDto))
                        .sorted(Comparator.comparing(FlightDto::getTravelDate).reversed())
                        .collect(Collectors.toList());
        List<FlightDto> departingFlights =
                flights.stream()
                        .filter(flightDto -> flightDto.getDepartureFrom().equals(airportDto))
                        .sorted(Comparator.comparing(FlightDto::getTravelDate).reversed())
                        .collect(Collectors.toList());
        mav.addObject("arrivingFlights", arrivingFlights);
        mav.addObject("departingFlights", departingFlights);
        mav.addObject("selectedAirport", airportId);
        return mav;
    }

    @GetMapping("/search")
    public ModelAndView searchFlight(HttpServletRequest request, HttpServletResponse res) {
        ModelAndView mav = new ModelAndView("customerdashboard");
        List<AirportDto> airports = airportService.getAllAirport();
        mav.addObject("sources", airports);
        mav.addObject("destinations", airports);
        List<String> errors = new ArrayList<>();
        SearchFlightDto searchFlightDto = new SearchFlightDto();
        String sortfilter = request.getParameter("sortfilter");
        if (!StringUtils.isEmpty(sortfilter)) {
            readSortFilterAttributes(request, errors, searchFlightDto);
            readDataSession(request, errors, searchFlightDto);
        } else {
            readDataFromRequest(request, errors, searchFlightDto);
        }
        if (!errors.isEmpty()) {
            mav.addObject("errors", errors);
            return mav;
        }

        List<FlightDto> srcToDestFlights = flightService.search(searchFlightDto, false);

        //        List<FlightDto> destToSrcFlights = null;
        //        if(Objects.nonNull(searchFlightDto.getReturnDate())) {
        //            destToSrcFlights = flightService.search(searchFlightDto, true);
        //        }

        if (Objects.isNull(srcToDestFlights) || srcToDestFlights.isEmpty()) {
            errors.add("No flights found for this search");
            mav.addObject("errors", errors);
            return mav;
        }

        mav.addObject("srcToDestFlights", srcToDestFlights);

        //        if(Objects.nonNull(destToSrcFlights) && !destToSrcFlights.isEmpty()) {
        //            mav.addObject("destToSrcFlights", destToSrcFlights);
        //        }

        List<Integer> sortedFares = flightService.getSortedFares(srcToDestFlights, searchFlightDto);
        Integer lowestFlightCost = sortedFares.get(0);
        Integer highestFlightCost = sortedFares.get(sortedFares.size() - 1);
        mav.addObject("lowestFlightCost", lowestFlightCost);
        mav.addObject("highestFlightCost", highestFlightCost);

        mav.addObject("airlines", flightService.getAirlines(srcToDestFlights));
        mav.addObject("seatPreference", (String) request.getSession().getAttribute("seatPreference"));
        mav.addObject("sourceAirportId", (Long) request.getSession().getAttribute("sourceAirportId"));
        mav.addObject("destinationAirportId", (Long) request.getSession().getAttribute("destinationAirportId"));
        mav.addObject("travelDate", (LocalDate) request.getSession().getAttribute("travelDate"));
        mav.addObject("sortBy", searchFlightDto.getSortBy());
        mav.addObject("filterAirlines", String.join(",", searchFlightDto.getAirlines()));

        return mav;
    }

    private void readSortFilterAttributes(HttpServletRequest request, List<String> errors,
                                          SearchFlightDto searchFlightDto) {
        String sortBy = request.getParameter("sortBy");
        String filterMinPrice = request.getParameter("filterMinPrice");
        String filterMaxPrice = request.getParameter("filterMaxPrice");
        String filterAirlines = request.getParameter("filterAirlines");
        if (!StringUtils.isEmpty(sortBy)) {
            searchFlightDto.setSortBy(SearchFlightConstants.SortBy.valueOf(sortBy.trim()));
        }
        if (!StringUtils.isEmpty(filterMinPrice)) {
            searchFlightDto.setMinPrice(Integer.parseInt(filterMinPrice.trim()));
        }
        if (!StringUtils.isEmpty(filterMaxPrice)) {
            searchFlightDto.setMaxPrice(Integer.parseInt(filterMaxPrice.trim()));
        }
        if (!StringUtils.isEmpty(filterAirlines)) {
            searchFlightDto.setAirlines(Arrays.asList(filterAirlines.trim().split(",")));
        }
    }

    private void readDataFromRequest(HttpServletRequest request, List<String> errors, SearchFlightDto searchFlightDto) {
        String sourceAirport = request.getParameter("source");
        String destinationAirport = request.getParameter("destination");
        String travelDateStr = request.getParameter("traveldate");
        String returnDateStr = request.getParameter("returndate");
        String seatPreference = request.getParameter("class");

        Long sourceAirportId = null;
        if (StringUtils.isEmpty(sourceAirport)) {
            errors.add("Please enter source airport");
        } else {
            try {
                sourceAirport = sourceAirport.trim();
                sourceAirportId = Long.parseLong(sourceAirport);
            } catch (Exception e) {
                errors.add("Please enter valid source airport");
            }
        }
        Long destinationAirportId = null;
        if (StringUtils.isEmpty(destinationAirport)) {
            errors.add("Please enter destination airport");
        } else {
            try {
                destinationAirport = destinationAirport.trim();
                destinationAirportId = Long.parseLong(destinationAirport);
            } catch (Exception e) {
                errors.add("Please enter valid destination airport");
            }
        }
        LocalDate travelDate = null;
        if (StringUtils.isEmpty(travelDateStr)) {
            errors.add("Please enter travel date");
        } else {
            try {
                travelDateStr = travelDateStr.trim();
                travelDate = DateUtil.parseDate(travelDateStr);
            } catch (Exception e) {
                errors.add("Please enter valid travel date in YYYY-MM-DD format");
            }
        }
        LocalDate returnDate = null;
        if (!StringUtils.isEmpty(returnDateStr)) {
            try {
                returnDateStr = returnDateStr.trim();
                returnDate = DateUtil.parseDate(returnDateStr);
            } catch (Exception e) {
                errors.add("Please enter valid return date in YYYY-MM-DD format");
            }
        }
        if (StringUtils.isEmpty(seatPreference)) {
            errors.add("Please select preferred class for search");
        } else if (!ALLOWED_SEAT_PREFERENCES.contains(seatPreference.trim())) {
            errors.add("Please select valid preferred class for search");
        }

        if (errors.isEmpty()) {
            searchFlightDto.setSourceAirportId(sourceAirportId);
            searchFlightDto.setDestinationAirportId(destinationAirportId);
            searchFlightDto.setTravelDate(travelDate);
            searchFlightDto.setReturnDate(returnDate);
            searchFlightDto.setSeatPreference(SearchFlightConstants.SeatPreference.valueOf(seatPreference.trim()));

            request.getSession().setAttribute("sourceAirportId", sourceAirportId);
            request.getSession().setAttribute("destinationAirportId", destinationAirportId);
            request.getSession().setAttribute("travelDate", travelDate);
            request.getSession().setAttribute("returnDate", returnDate);
            request.getSession().setAttribute("seatPreference", seatPreference.trim());
        }
    }

    private void readDataSession(HttpServletRequest request, List<String> errors, SearchFlightDto searchFlightDto) {
        HttpSession session = request.getSession();
        searchFlightDto.setSourceAirportId((Long) session.getAttribute("sourceAirportId"));
        searchFlightDto.setDestinationAirportId((Long) session.getAttribute("destinationAirportId"));
        searchFlightDto.setTravelDate((LocalDate) session.getAttribute("travelDate"));
        Object returnDateObj = session.getAttribute("returnDate");
        if (Objects.nonNull(returnDateObj)) {
            searchFlightDto.setReturnDate((LocalDate) returnDateObj);
        }
        searchFlightDto.setSeatPreference(
                SearchFlightConstants.SeatPreference
                        .valueOf((String) session.getAttribute("seatPreference")));
    }
}