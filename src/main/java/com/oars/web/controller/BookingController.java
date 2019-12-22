package com.oars.web.controller;

import com.oars.constant.BookingStatus;
import com.oars.constant.Role;
import com.oars.constant.SearchFlightConstants;
import com.oars.dto.ActiveFlightDto;
import com.oars.dto.AirlineRevenueDto;
import com.oars.dto.AirportDto;
import com.oars.dto.BookingDto;
import com.oars.dto.CustomerRevenueDto;
import com.oars.dto.FlightDto;
import com.oars.dto.FlightRevenueDto;
import com.oars.dto.UserDto;
import com.oars.service.AirportService;
import com.oars.service.BookingService;
import com.oars.service.FlightService;
import com.oars.service.UserService;
import com.oars.util.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private AirportService airportService;

    @Autowired
    private UserService userService;

    @Autowired
    private FlightService flightService;

    @GetMapping("/user/pastBookings")
    public ModelAndView getAllPastBookingsForCurrentUser(HttpServletRequest request, HttpServletResponse res) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (Objects.isNull(userId)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Please login first to view your past bookings");
            return mav;
        }
        List<BookingDto> pastBookings = bookingService.getAllPastBookingsForUser(userId);
        List<String> errors = new ArrayList<>();
        ModelAndView mav = new ModelAndView("customerpastbookings");
        if (pastBookings.isEmpty()) {
            errors.add("No booking found");
            mav.addObject("errors", errors);
        } else {
            mav.addObject("pastBookings", pastBookings);
        }
        return mav;
    }

    @GetMapping({"/user/upcomingBookings"})
    public ModelAndView getAllUpcomingBookingsForCurrentUser(HttpServletRequest request, HttpServletResponse res) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (Objects.isNull(userId)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Please login first to view your upcoming bookings");
            return mav;
        }
        List<BookingDto> upcomingBookings = bookingService.getAllUpcomingBookingsForUser(userId);
        List<String> errors = new ArrayList<>();
        ModelAndView mav = new ModelAndView("customerupcomingbookings");
        if (upcomingBookings.isEmpty()) {
            errors.add("No booking found");
            mav.addObject("errors", errors);
        } else {
            mav.addObject("upcomingBookings", upcomingBookings);
        }
        return mav;
    }

    @GetMapping("/user/cancelBooking")
    public ModelAndView getAllUpcomingBookingsToCancelForCurrentUser(HttpServletRequest request,
                                                                     HttpServletResponse res) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (Objects.isNull(userId)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Please login first to cancel flight");
            return mav;
        }
        List<BookingDto> upcomingBookings = bookingService.getAllUpcomingBookingsForUser(userId);
        upcomingBookings.removeIf(bookingDto -> Objects.equals(BookingStatus.CANCELLED.name(), bookingDto.getStatus()));
        ModelAndView mav = new ModelAndView("customercancelbooking");
        List<String> errors = new ArrayList<>();
        if (upcomingBookings.isEmpty()) {
            errors.add("No booking found");
            mav.addObject("errors", errors);
        } else {
            mav.addObject("upcomingBookings", upcomingBookings);
        }
        return mav;
    }

    @PostMapping("/create")
    public ModelAndView createBooking(HttpServletRequest request, HttpServletResponse res) {
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (Objects.isNull(userId)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Please login first to book flight");
            return mav;
        }
        String role = (String) request.getSession().getAttribute("role");
        if (Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            userId = Long.parseLong(request.getParameter("customer"));
        }
        Long flightId = Long.parseLong(request.getParameter("flight"));
        String seatPreference = (String) request.getSession().getAttribute("seatPreference");
        LocalDate travelDate = (LocalDate) request.getSession().getAttribute("travelDate");

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        FlightDto flightDto = new FlightDto();
        flightDto.setId(flightId);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setUser(userDto);
        bookingDto.setFlight(flightDto);
        bookingDto.setSeatClass(seatPreference);
        bookingDto.setTravelDate(travelDate);

        bookingDto = bookingService.createBooking(bookingDto);

        ModelAndView mav = null;
        if (Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            mav = makeReservationForCustomer(request, res);
        } else {
            mav = new ModelAndView("customerdashboard");
            List<AirportDto> airports = airportService.getAllAirport();
            mav.addObject("sources", airports);
            mav.addObject("destinations", airports);
        }

        if (Objects.equals(BookingStatus.WAITING.name(), bookingDto.getStatus())) {
            List<String> errors = new ArrayList<>();
            errors.add("No seat available for selected flight and selected seat preference while booking");
            mav.addObject("errors", errors);
            mav.addObject("message", "Booking is not confirmed yet. You are in waiting list");
        } else {
            log.info("booking created successfully");
            mav.addObject("message", "flight booked successfully");
        }
        return mav;
    }

    @PostMapping("/edit")
    public ModelAndView updateBooking(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        Long bookingId = Long.parseLong(request.getParameter("bookingId"));
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(bookingId);

        Long flightId = null;
        String flightIdStr = request.getParameter("flight");
        if(!StringUtils.isEmpty(flightIdStr)) {
            flightId = Long.parseLong(flightIdStr);
            FlightDto flight = new FlightDto();
            flight.setId(flightId);
            bookingDto.setFlight(flight);
        }
        SearchFlightConstants.SeatPreference seatClass = null;
        String seatClassStr = request.getParameter("seatClass");
        if(!StringUtils.isEmpty(seatClassStr)) {
            seatClass = SearchFlightConstants.SeatPreference.valueOf(seatClassStr);
            bookingDto.setSeatClass(seatClass.name());
        }
        BookingStatus status = null;
        String statusStr = request.getParameter("status");
        if(!StringUtils.isEmpty(statusStr)) {
            status = BookingStatus.valueOf(statusStr);
            bookingDto.setStatus(status.name());
        }
        bookingService.updateBooking(bookingDto);
        return modifyCustomerBookings(request, res);
    }

    @RequestMapping(value = "/agent/modify", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView modifyCustomerBookings(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("modifycustomerbooking");
        List<String> errors = new ArrayList<>();
        List<UserDto> customers = userService.getAllCustomers();
        if (customers.isEmpty()) {
            errors.add("No customers found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("customers", customers);
        String customerIdStr = request.getParameter("customer");
        if (StringUtils.isEmpty(customerIdStr)) {
            return mav;
        }
        Long customerId = Long.parseLong(customerIdStr);
        mav.addObject("selectedCustomer", customerId);

        List<BookingDto> bookings = bookingService.getAllUpcomingBookingsForUser(customerId);
        bookings =
                bookings.stream()
                        .filter(bookingDto -> !Objects.equals(BookingStatus.CANCELLED.name(), bookingDto.getStatus()))
                        .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            errors.add("No upcoming bookings found for this user");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("bookings", bookings);

        String bookingIdStr = request.getParameter("flightBookingId");
        if(StringUtils.isEmpty(bookingIdStr)) {
            return mav;
        }
        Long bookingId = Long.parseLong(bookingIdStr);
        mav.addObject("selectedBooking", bookingId);

        LocalDate travelDate = DateUtil.parseDate(request.getParameter("travelDate"));
        SearchFlightConstants.SeatPreference seatPreference = SearchFlightConstants.SeatPreference.valueOf(request.getParameter("class"));
        List<FlightDto> flights = flightService.search(bookingId, travelDate, seatPreference);
        if (Objects.isNull(flights) || flights.isEmpty()) {
            errors.add("No flights found for this search");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("flights", flights);
        mav.addObject("selectedTravelDate", travelDate);
        mav.addObject("selectedSeatClass", seatPreference.name());

        return mav;
    }

    @RequestMapping(value = "/agent/book", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView makeReservationForCustomer(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        request.getSession().removeAttribute("sourceAirportId");
        request.getSession().removeAttribute("destinationAirportId");
        request.getSession().removeAttribute("travelDate");
        request.getSession().removeAttribute("returnDate");
        request.getSession().removeAttribute("seatPreference");

        ModelAndView mav = new ModelAndView("agentbooking");
        List<String> errors = new ArrayList<>();
        List<UserDto> customers = userService.getAllCustomers();
        if (customers.isEmpty()) {
            errors.add("No customers found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("customers", customers);

        String customerIdStr = request.getParameter("customer");
        if(!StringUtils.isEmpty(customerIdStr)) {
            Long customerId = Long.parseLong(customerIdStr);
            mav.addObject("selectedCustomer", customerId);
        }
        List<AirportDto> airports = airportService.getAllAirport();
        mav.addObject("sources", airports);
        mav.addObject("destinations", airports);
        return mav;
    }

    @PostMapping("/delete")
    public ModelAndView cancelBooking(HttpServletRequest request, HttpServletResponse res) {
        List<String> errors = new ArrayList<>();
        Long userId = (Long) request.getSession().getAttribute("userId");
        if (Objects.isNull(userId)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Please login first to cancel booking");
            return mav;
        }
        String userRole = (String) request.getSession().getAttribute("role");
        Long bookingId = Long.parseLong(request.getParameter("bookingId"));
        if (Objects.equals(userRole, Role.CUSTOMER.name())) {
            bookingService.deleteBooking(bookingId, userId, errors);
        } else {
            log.error("Non-customer user tried to cancel booking {}", bookingId);
            errors.add("You don't have permission to cancel this booking");
        }
        List<AirportDto> airports = airportService.getAllAirport();
        ModelAndView mav = new ModelAndView("customerdashboard");
        mav.addObject("sources", airports);
        mav.addObject("destinations", airports);
        if (!errors.isEmpty()) {
            mav.addObject("errors", errors);
        } else {
            log.info("Booking deleted successfully");
            mav.addObject("message", "flight booking deleted successfully");
        }
        return mav;
    }

    @GetMapping("/activeflights")
    public ModelAndView getActiveFlights(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("activeflights");
        List<String> errors = new ArrayList<>();

        List<ActiveFlightDto> flights = bookingService.findFlightsSortedByHighestBookings();
        if (flights.isEmpty()) {
            errors.add("No records found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("flights", flights);
        return mav;
    }

    @GetMapping("/customerrevenuereport")
    public ModelAndView getCustomersWithMostRevenue(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("customerrevenuereport");
        List<String> errors = new ArrayList<>();

        List<CustomerRevenueDto> customers = bookingService.findCustomerSortedByHighestRevenue();
        if (customers.isEmpty()) {
            errors.add("No records found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("customers", customers);
        return mav;
    }

    @GetMapping("/airlinerevenuereport")
    public ModelAndView getAirlinesWithMostRevenue(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("airlinerevenuereport");
        List<String> errors = new ArrayList<>();

        List<AirlineRevenueDto> airlines = bookingService.findAirlineSortedByHighestRevenue();
        if (airlines.isEmpty()) {
            errors.add("No records found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("airlines", airlines);
        return mav;
    }

    @GetMapping("/flightrevenuereport")
    public ModelAndView getFlightssWithMostRevenue(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("flightrevenuereport");
        List<String> errors = new ArrayList<>();

        List<FlightRevenueDto> flights = bookingService.findFlightsSortedByHighestRevenue();
        if (flights.isEmpty()) {
            errors.add("No records found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("flights", flights);
        return mav;
    }

    @RequestMapping(value = "/customer", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getCustomerBookings(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("customerbookings");
        List<String> errors = new ArrayList<>();
        List<UserDto> customers = userService.getAllCustomers();
        if (customers.isEmpty()) {
            errors.add("No records found in the system");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("customers", customers);
        String customerIdStr = request.getParameter("customer");
        if (Objects.isNull(customerIdStr)) {
            return mav;
        }
        Long customerId = Long.parseLong(customerIdStr);
        List<BookingDto> bookings = bookingService.findAllByUser(customerId);
        if (bookings.isEmpty()) {
            errors.add("No bookings found for this user");
            mav.addObject("errors", errors);
            return mav;
        }

        mav.addObject("bookings", bookings);
        mav.addObject("selectedCustomer", customerId);
        return mav;
    }

    @RequestMapping(value = "/flight", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getFlightBookings(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("flightbookings");
        List<String> errors = new ArrayList<>();
        List<FlightDto> flights = flightService.getAllFlights();
        if (flights.isEmpty()) {
            errors.add("No flights found");
            mav.addObject("errors", errors);
            return mav;
        }
        mav.addObject("flights", flights);
        String flightIdStr = request.getParameter("flight");
        if (Objects.isNull(flightIdStr)) {
            return mav;
        }
        Long flightId = Long.parseLong(flightIdStr);
        List<BookingDto> bookings = bookingService.findAllByFlightId(flightId);
        if (bookings.isEmpty()) {
            errors.add("No bookings found for this flight");
            mav.addObject("errors", errors);
            return mav;
        }

        mav.addObject("bookings", bookings);
        mav.addObject("selectedFlight", flightId);
        return mav;
    }

    @RequestMapping(value = "/month", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView getMonthlyBookings(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }

        ModelAndView mav = new ModelAndView("monthlybookings");
        List<String> errors = new ArrayList<>();
        String selectedYearStr = request.getParameter("selectedYear");
        String selectedMonthStr = request.getParameter("selectedMonth");
        if (Objects.isNull(selectedYearStr)) {
            errors.add("Please select year");
            mav.addObject("errors", errors);
        }
        if (Objects.isNull(selectedMonthStr)) {
            errors.add("Please select month");
            mav.addObject("errors", errors);
            return mav;
        }
        int selectedMonth = Integer.parseInt(selectedMonthStr) + 1;
        int selectedYear = Integer.parseInt(selectedYearStr);
        LocalDate startDateOfMonth =
                LocalDate.of(selectedYear, selectedMonth, 1);
        LocalDate lastDateOfMonth =
                LocalDate.of(selectedYear, selectedMonth,
                        DateUtil.lastDayOfMonth(selectedYear, selectedMonth));
        List<BookingDto> bookings = bookingService.findAllByBookingDateBetween(startDateOfMonth, lastDateOfMonth);
        if (bookings.isEmpty()) {
            errors.add("No bookings found for this month");
            mav.addObject("errors", errors);
            return mav;
        }

        mav.addObject("bookings", bookings);
        mav.addObject("selectedYear", selectedYear);
        mav.addObject("selectedMonth", selectedMonth-1);
        return mav;
    }

}