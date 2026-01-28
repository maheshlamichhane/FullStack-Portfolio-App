package com.portfolio.app.exception;

import com.portfolio.app.dto.ErrorResponseDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomJwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (authException != null && authException.getMessage() != null) ? authException.getMessage()
                : "Unauthorized";
        String path = request.getRequestURI();
        response.setHeader("it-company-error-reason", "Authentication failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");


        // Construct the JSON response
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO();
        errorResponseDTO.setMessage(message);
        errorResponseDTO.setCode(HttpStatus.BAD_REQUEST.value());

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponseDTO));
    }
}
