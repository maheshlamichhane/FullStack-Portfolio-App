package com.portfolio.app.service;


import com.portfolio.app.dao.CustomerDAO;
import com.portfolio.app.entity.Customer;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class UserDetailService implements UserDetailsService {

    private final CustomerDAO customerDAO;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Customer customer = customerDAO.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<SimpleGrantedAuthority> grantedAuthorities = Stream.concat(
                customer.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())),
                customer.getAuthorities().stream().map(auth -> new SimpleGrantedAuthority(auth.getName()))
        ).collect(Collectors.toSet());

        return new User(
                customer.getEmail(),
                customer.getPassword(),
                customer.isEnabled(),
                true, true, true,
                grantedAuthorities
        );
    }
}
