package com.sobow.secureweb.controllers;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

    @GetMapping(path = "/api/csrf")
    public CsrfToken csrf(CsrfToken token){
        return token;
    }
}
