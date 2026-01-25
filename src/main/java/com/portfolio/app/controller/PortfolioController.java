package com.portfolio.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PortfolioController {

    @GetMapping("/sayHi")
    public String sayHi() {
        return "Hi there!";
    }
}
