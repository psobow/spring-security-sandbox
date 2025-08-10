package com.sobow.secureweb.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping(path = "/")
    public String dashboard(Model model) {
        model.addAttribute("creditCardNumber", "1234-5678-9012-3456");
        model.addAttribute("salary", "$999,000");
        return "dashboard";
    }
}
