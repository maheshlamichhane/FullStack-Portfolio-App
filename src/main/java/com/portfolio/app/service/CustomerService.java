package com.portfolio.app.service;


import com.portfolio.app.dto.CustomerRegisterRequestDTO;
import com.portfolio.app.dto.CustomerResponseDTO;

public interface CustomerService {
    CustomerResponseDTO registerCustomer(CustomerRegisterRequestDTO request);
}
