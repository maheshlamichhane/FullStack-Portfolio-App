package com.portfolio.app.controller;

import com.portfolio.app.dto.CustomerRegisterRequestDTO;
import com.portfolio.app.dto.CustomerResponseDTO;
import com.portfolio.app.service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponseDTO registerCustomer(@Valid @RequestBody CustomerRegisterRequestDTO customerRegisterRequestDTO){
        return customerService.registerCustomer(customerRegisterRequestDTO);
    }

}
