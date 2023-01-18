package com.oakenscience.todoapp.controllers;

import com.oakenscience.todoapp.dto.GenericResponse;
import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.error.UserAlreadyExistException;
import com.oakenscience.todoapp.models.User;
import com.oakenscience.todoapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;


    Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user")
    @ResponseBody
    public Principal user(Principal user) {
        return user;
    }

    @PostMapping("/user/registration")
    public GenericResponse registerUserAccount(
            @Valid UserDto accountDto, HttpServletRequest request) {

        logger.debug("Registering user account with information: {}", accountDto);
//        if (bindingResult.hasErrors()) {
//            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
//        }

        try {
            userService.registerNewUserAccount(accountDto);
        } catch (UserAlreadyExistException ex) {
            return new GenericResponse("Email already exists", ex.getMessage());
        }

        return new GenericResponse("success");
    }
}
