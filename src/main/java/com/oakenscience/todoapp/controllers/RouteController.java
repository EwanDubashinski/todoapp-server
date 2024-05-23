package com.oakenscience.todoapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class RouteController {
//    @RequestMapping(value = {"/{path:[^\\.]*}", "/{path:(?!api).*}", "/*/**"})
//    @RequestMapping(value = "/{path:[^\\.]*}") //   {path:(?:(?!api|.).)*}/**
    @RequestMapping(value = "/**/{path:[^\\.]*}")
    public String redirect(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return "forward:/";
    }
}
