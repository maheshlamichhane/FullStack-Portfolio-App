package com.portfolio.app.service;


import com.portfolio.app.dao.CustomerDAO;
import com.portfolio.app.dto.CustomerRegisterRequestDTO;
import com.portfolio.app.dto.CustomerResponseDTO;
import com.portfolio.app.entity.Customer;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerDAO customerDAO;
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public CustomerResponseDTO registerCustomer(CustomerRegisterRequestDTO request) {

        if (customerDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Customer user = Customer.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .enabled(true)
//                .role(request.getRole())
                .build();

        Customer savedUser = customerDAO.save(user);

        return CustomerResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .build();
    }
}
