package com.oakenscience.todoapp.controllers;

import com.oakenscience.todoapp.dto.GenericResponse;
import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.models.User;
import com.oakenscience.todoapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;


    Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/user/registration")
    public GenericResponse registerUserAccount(
            @Valid UserDto accountDto, HttpServletRequest request) {

        logger.debug("Registering user account with information: {}", accountDto);

        final User registered = userService.registerNewUserAccount(accountDto);


        return new GenericResponse("success");
    }
}
