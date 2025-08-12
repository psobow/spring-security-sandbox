package com.sobow.secureweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {
    
    @GetMapping(path = "/logout")
    public String logout() {
        return "logout";
    }
}
