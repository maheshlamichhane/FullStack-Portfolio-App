package com.portfolio.app.dto;

import lombok.Data;

@Data
public class CustomerRegisterRequestDTO {
    private String username;
    private String email;
    private String password;

}
