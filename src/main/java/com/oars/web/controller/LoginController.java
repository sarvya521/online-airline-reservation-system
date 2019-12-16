package com.oars.web.controller;

import com.oars.constant.Role;
import com.oars.dto.AirportDto;
import com.oars.dto.UserDto;
import com.oars.service.AirportService;
import com.oars.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

@Log4j2
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private AirportService airportService;

    @GetMapping("/")
    public ModelAndView welcome() {
        return new ModelAndView("login");
    }

    @GetMapping("/home")
    public ModelAndView customerHome() {
        List<AirportDto> airports = airportService.getAllAirport();
        ModelAndView mav = new ModelAndView("customerdashboard");
        mav.addObject("sources", airports);
        mav.addObject("destinations", airports);
        return mav;
    }

    @GetMapping("/adminhome")
    public ModelAndView adminHome() {
        return new ModelAndView("admindashboard");
    }

    @GetMapping("/agenthome")
    public ModelAndView agentHome() {
        return new ModelAndView("agentdashboard");
    }

    @PostMapping("/checkLogin")
    public ModelAndView checkLogin(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        UserDto userDto = userService.checkLogin(email, password);
        if (Objects.nonNull(userDto)) {
            Long userId = userDto.getId();
            String firstName = userDto.getFirstName();
            String lastName = userDto.getLastName();
            String role = userDto.getRole();
            request.getSession().setAttribute("userId", userId);
            request.getSession().setAttribute("email", email);
            request.getSession().setAttribute("firstName", firstName);
            request.getSession().setAttribute("lastName", lastName);
            request.getSession().setAttribute("role", role);
            log.info("Session saved: " + firstName + " " + lastName);
            if (Objects.equals(Role.ADMIN.name(), role)) {
                return new ModelAndView("admindashboard");
            } else if (Objects.equals(Role.CUSTOMER_REPRESENTATIVE.name(), role)) {
                return new ModelAndView("agentdashboard");
            } else {
                List<AirportDto> airports = airportService.getAllAirport();
                ModelAndView mav = new ModelAndView("customerdashboard");
                mav.addObject("sources", airports);
                mav.addObject("destinations", airports);
                return mav;
            }
        } else {
            String message = "Login failed. Please enter correct credentials";
            return new ModelAndView("unsuccessfulLogin", "message", message);
        }
    }

    @GetMapping("/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @PostMapping("/register")
    public ModelAndView registrationDetails(HttpServletRequest request, HttpServletResponse res) {
        String email = request.getParameter("emailAddress");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        String role = Role.CUSTOMER.name();
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword(password);
        userDto.setRole(role);

        boolean ifUserExists = userService.checkIfUserExists(userDto.getEmail());
        if (ifUserExists) {
            String message = email + " already exists";
            return new ModelAndView("unsuccessfulRegistration", "message", message);
        } else {
            userDto = userService.createUser(userDto);
            request.getSession().setAttribute("userId", userDto.getId());
            request.getSession().setAttribute("email", email);
            request.getSession().setAttribute("firstName", firstName);
            request.getSession().setAttribute("lastName", lastName);
            request.getSession().setAttribute("role", role);
            log.info("User registered successfully");
            List<AirportDto> airports = airportService.getAllAirport();
            ModelAndView mav = new ModelAndView("customerdashboard");
            mav.addObject("sources", airports);
            mav.addObject("destinations", airports);
            return mav;
        }
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest request,
                               HttpServletResponse response) {
        request.getSession().invalidate();
        log.info("Logged out...");
        return new ModelAndView("login");
    }
}