package com.oars.web.controller;

import com.oars.dto.UserDto;
import com.oars.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

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