package com.oars.web.controller;

import com.oars.constant.Role;
import com.oars.dto.UserDto;
import com.oars.service.UserService;
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
@RequestMapping("/user")
public class UserController {

    private static final String DEFAULT_PASSWORD = "123456";

    @Autowired
    private UserService userService;

    @GetMapping("/customer/crud")
    public ModelAndView crudCustomer(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        ModelAndView mav = new ModelAndView("customer");
        List<UserDto> customers = userService.getAllCustomers();
        customers =
                customers.stream()
                        .sorted(Comparator.comparing(UserDto::getId).reversed())
                        .collect(Collectors.toList());
        mav.addObject("customers", customers);
        return mav;
    }

    @PostMapping("/customer/addedit")
    public ModelAndView addEditCustomer(HttpServletRequest request, HttpServletResponse res) {
        String loggedInUser = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), loggedInUser)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        String email = request.getParameter("customerEmail");
        String firstName = request.getParameter("customerFirstName");
        String lastName = request.getParameter("customerLastName");

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);

        String customerIdStr = request.getParameter("customerId");
        if (StringUtils.isEmpty(customerIdStr)) {
            String password = DEFAULT_PASSWORD;
            String role = Role.CUSTOMER.name();
            userDto.setPassword(password);
            userDto.setRole(role);
            userService.createUser(userDto);
        } else {
            userDto.setId(Long.parseLong(customerIdStr));
            userService.updateUser(userDto);
        }
        return crudCustomer(request, res);
    }

    @PostMapping("/customer/delete")
    public ModelAndView deleteUser(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        Long customerId = Long.parseLong(request.getParameter("userId"));
        userService.deleteUser(customerId);
        return crudCustomer(request, res);
    }

    @GetMapping("/agent/crud")
    public ModelAndView crudAgent(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        ModelAndView mav = new ModelAndView("agent");
        List<UserDto> agents = userService.getAllAgents();
        agents =
                agents.stream()
                        .sorted(Comparator.comparing(UserDto::getId).reversed())
                        .collect(Collectors.toList());
        mav.addObject("agents", agents);
        return mav;
    }

    @PostMapping("/agent/addedit")
    public ModelAndView addEditAgent(HttpServletRequest request, HttpServletResponse res) {
        String loggedInUser = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), loggedInUser)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        String email = request.getParameter("agentEmail");
        String firstName = request.getParameter("agentFirstName");
        String lastName = request.getParameter("agentLastName");

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);

        String agentIdStr = request.getParameter("agentId");
        if (StringUtils.isEmpty(agentIdStr)) {
            String password = DEFAULT_PASSWORD;
            String role = Role.CUSTOMER_REPRESENTATIVE.name();
            userDto.setPassword(password);
            userDto.setRole(role);
            userService.createUser(userDto);
        } else {
            userDto.setId(Long.parseLong(agentIdStr));
            userService.updateUser(userDto);
        }
        return crudAgent(request, res);
    }

    @PostMapping("/agent/delete")
    public ModelAndView deleteAgent(HttpServletRequest request, HttpServletResponse res) {
        String role = (String) request.getSession().getAttribute("role");
        if (!Objects.equals(Role.ADMIN.name(), role)) {
            ModelAndView mav = new ModelAndView("error");
            mav.addObject("message", "Restricted Access. Or Your Session is expired. Try login again");
            return mav;
        }
        Long customerId = Long.parseLong(request.getParameter("userId"));
        userService.deleteUser(customerId);
        return crudAgent(request, res);
    }

    @PostMapping
    public ModelAndView createUser(HttpServletRequest request, HttpServletResponse res) {
        String email = request.getParameter("emailAddress");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
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
            userService.createUser(userDto);
            log.info("user created successfully");
            return new ModelAndView("homepage", "message", "user created successfully!");
        }
    }

    @PutMapping
    public ModelAndView updateUser(HttpServletRequest request, HttpServletResponse res) {
        Long userId = Long.parseLong(request.getParameter("userId"));
        String email = request.getParameter("emailAddress");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword(password);
        userDto.setRole(role);

        userService.updateUser(userDto);
        log.info("user updated successfully");
        return new ModelAndView("homepage", "message", "user updated successfully!");
    }

    @DeleteMapping("/{id}")
    public ModelAndView deleteUser(@PathVariable(value = "id") Long userId) {
        userService.deleteUser(userId);
        return new ModelAndView("homepage", "message", "user deleted successfully!");
    }
}