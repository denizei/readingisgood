package com.getir.readingisgood.controller;

import com.getir.readingisgood.authentication.service.JwtUserDetailsService;
import com.getir.readingisgood.authentication.util.JwtTokenUtil;
import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.JwtResponse;
import com.getir.readingisgood.model.ObjectResponse;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.model.request.JwtRequest;
import com.getir.readingisgood.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Validated
@CrossOrigin(origins = "http://localhost:8082")
@RestController
@Tag(name = "Authentication Operations", description = "The operations on authentication")
public class AuthenticationController {
    Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    @Operation(summary = "Authentication", description = "User authenticates and a token is created")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "400", description = "Wrong Credentials")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            Optional<Customer> customer = customerRepository.findByEmail(authenticationRequest.getEmail());
            if (customer.isEmpty() || !customer.get().getPassword().equals(
                    ControllerHelper.getHashedPassword(authenticationRequest.getPassword())))
                throw new BadCredentialsException("INVALID CREDENTIALS");
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getEmail());
            final String token = jwtTokenUtil.generateToken(userDetails);
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException ex) {
            logger.error(ex.getMessage(), ex);
            return ResponseEntity.badRequest().body(new ObjectResponse<>(
                    new GeneralException(GeneralException.ErrorCode.CUSTOMER_NOT_FOUND
                            , "User is not authenticated")));
        }
    }
}