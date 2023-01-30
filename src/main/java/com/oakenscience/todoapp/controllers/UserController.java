package com.oakenscience.todoapp.controllers;

import com.oakenscience.todoapp.dto.GenericResponse;
import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.listeners.RegistrationCompleteEvent;
import com.oakenscience.todoapp.models.DbUser;
import com.oakenscience.todoapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.security.Principal;

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

        DbUser dbUser = userService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new RegistrationCompleteEvent(dbUser));
        return new GenericResponse("success");
    }

    @GetMapping("/user/activate/{code}")
    public GenericResponse activate(@PathVariable String code){
        userService.activate(code);
        return new GenericResponse("success");
    }
}
