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

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Customer> cust = customerRepository.findByEmail(email);
        if (cust.isPresent()) {
            return new User(cust.get().getEmail(), cust.get().getPassword(),
                    new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(cust.get().getRole().name()))));
        } else {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}