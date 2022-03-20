package com.getir.readingisgood.authentication.service;


import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
/*
    UserDetailsService implementation
    Checks the customer from database and returns a UserDetails object with their role
    If a customer is not found, UsernameNotFoundException is thrown.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return new User(customer.get().getEmail(), customer.get().getPassword(),
                    new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(customer.get().getRole().name()))));
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}