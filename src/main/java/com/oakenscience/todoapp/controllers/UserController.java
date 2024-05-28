package com.oakenscience.todoapp.controllers;

import com.oakenscience.todoapp.dto.GenericResponse;
import com.oakenscience.todoapp.dto.UserDto;
import com.oakenscience.todoapp.listeners.RegistrationCompleteEvent;
import com.oakenscience.todoapp.models.DbUser;
import com.oakenscience.todoapp.service.AuthenticationService;
import com.oakenscience.todoapp.service.JwtService;
import com.oakenscience.todoapp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class UserController {
     static class LoginResponse {
        private String token;

        private long expiresIn;

        public String getToken() {
            return token;
        }

         public LoginResponse setToken(String token) {
             this.token = token;
             return this;
         }

         public long getExpiresIn() {
             return expiresIn;
         }

         public LoginResponse setExpiresIn(long expiresIn) {
             this.expiresIn = expiresIn;
             return this;
         }
     }

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public UserController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

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


//    @PostMapping("/signup")
//    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
//        User registeredUser = authenticationService.signup(registerUserDto);
//
//        return ResponseEntity.ok(registeredUser);
//    }

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


    @PostMapping("/user/login")
    public ResponseEntity<GenericResponse> authenticate(@RequestBody UserDto loginUserDto, HttpServletResponse response) {
        DbUser authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        // Set the JWT as an HTTP-only cookie
        int expiryDuration = (int) TimeUnit.MILLISECONDS.toSeconds(jwtService.getExpirationTime());
        Cookie cookie = new Cookie("token", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Ensure this is true in production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(expiryDuration);
        cookie.setAttribute("SameSite", "strict");  //setSameSite("Strict");

        response.addCookie(cookie);

        return ResponseEntity.ok(new GenericResponse("success"));
    }
    @GetMapping("/user/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
//        request.getCookies()
        int expiryDuration = (int) TimeUnit.MILLISECONDS.toSeconds(jwtService.getExpirationTime());
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(expiryDuration);
        cookie.setAttribute("SameSite", "strict");

//        TODO: move cookie setter to detached method
//        TODO: add JWT invalidation

        response.addCookie(cookie);
        response.addHeader("Location", "/login");

        response.setStatus(HttpStatus.SEE_OTHER.value());
    }
}
