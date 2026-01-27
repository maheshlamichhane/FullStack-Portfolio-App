package com.portfolio.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ITCompanyController {

    @GetMapping("/company")
    public String sayHi(){
        return "Fetched data from db for company";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication){
        if(authentication != null){
            model.addAttribute("username",authentication.getName());
            model.addAttribute("roles",authentication.getAuthorities().toString());
        }
        return "dashboard";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    public String displayLoginPage(@RequestParam(value = "error", required = false) String error,
                                   @RequestParam(value = "logout", required = false) String logout, Model model) {
        String errorMessge = null;
        if(null != error) {
            errorMessge = "Username or Password is incorrect !!";
        }
        if(null!= logout) {
            errorMessge = "You have been successfully logged out !!";
        }
        model.addAttribute("errorMessge", errorMessge);
        return "login";
    }
}
