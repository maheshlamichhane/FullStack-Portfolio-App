package com.portfolio.app.dao;

import com.portfolio.app.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerDAO extends JpaRepository<Customer, Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Customer> findByUsername(String username);
    Optional<Customer> findByEmail(String username);
}
