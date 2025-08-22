package com.sobow.secureweb.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class PathMatchingApiController {
    
    @GetMapping(path = "/public-data")
    public String getPublicData() {
        return "This is public data!";
    }
    
    @GetMapping(path = "/private-data")
    public String getPrivateData() {
        return "This is private data";
    }
}
