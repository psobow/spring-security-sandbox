package com.sobow.secureweb.controllers;

import com.sobow.secureweb.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping(path = "/")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("creditCardNumber", "1234-5678-9012-3456");
        model.addAttribute("salary", user.getUser().getProfile().getSalary());
        return "dashboard";
    }
}
