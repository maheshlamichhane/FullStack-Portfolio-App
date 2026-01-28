package com.portfolio.app.controller;

import com.portfolio.app.dto.LoginRequestDTO;
import com.portfolio.app.dto.LoginResponseDTO;
import com.portfolio.app.util.JwtHelper;
import com.portfolio.app.util.KeyUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final KeyUtil keyUtil;
    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> apiLogin (@RequestBody LoginRequestDTO loginRequest) {

        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        if (null != authenticationResponse && authenticationResponse.isAuthenticated()) {

            if (keyUtil != null) {

                Collection<? extends GrantedAuthority> authorities = authenticationResponse.getAuthorities();
                List<String> roles = authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

                Map<String, Object> map = new HashMap<>();
                map.put("roles", roles);
                String jwtToken = jwtHelper.generateToken(authenticationResponse.getName(), map);

                LoginResponseDTO loginResponseDTO = new LoginResponseDTO();
                loginResponseDTO.setToken(jwtToken);
                loginResponseDTO.setExpiration("100");
                loginResponseDTO.setUsername(loginRequest.username());
                loginResponseDTO.setRefreshToken("");

                return ResponseEntity.status(HttpStatus.OK).body(loginResponseDTO);

            }

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping("/test")
    public String test(){
        return "Test Successful";
    }

}
