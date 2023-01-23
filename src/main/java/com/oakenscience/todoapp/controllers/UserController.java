package com.oakenscience.todoapp.controllers;

import com.oakenscience.todoapp.dto.GenericResponse;
import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.error.UserAlreadyExistException;
import com.oakenscience.todoapp.listeners.RegistrationCompleteEvent;
import com.oakenscience.todoapp.models.User;
import com.oakenscience.todoapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.validation.BindingResult;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
////            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
//            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
//            List<ObjectError> globalErrors = bindingResult.getGlobalErrors();
//        }

        User user = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(user));
        return new GenericResponse("success");
    }
}
