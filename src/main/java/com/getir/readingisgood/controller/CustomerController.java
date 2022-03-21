package com.getir.readingisgood.controller;

import com.getir.readingisgood.controller.advice.ChangeLogSaver;
import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.domain.CustomerRole;
import com.getir.readingisgood.domain.CustomerStatus;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.ObjectResponse;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.model.request.CustomerRequest;
import com.getir.readingisgood.repository.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Validated
@CrossOrigin(origins = "http://localhost:8082")
@RestController
@RequestMapping("/api")
@Tag(name = "Customer Operations", description = "The operations on authentication")
public class CustomerController {

    Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ChangeLogSaver changeLogSaver;


    @PostMapping("/customer")
    @Operation(summary = "Creates a customer with role ROLE_USER in  database", description = "No authorization needed")
    @ApiResponse(responseCode = "201", description = "Customer is created")
    @ApiResponse(responseCode = "208", description = "Customer is already in database")
    public ResponseEntity<ObjectResponse<Customer>> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        Optional<Customer> customer = customerRepository.findByEmail(customerRequest.getEmail());
        if (customer.isPresent())
            return new ResponseEntity<>(new ObjectResponse<>(208,
                    new GeneralException(GeneralException.ErrorCode.USER_ALREADY_EXISTS
                            , customerRequest.getEmail() + " is already used.")), HttpStatus.ALREADY_REPORTED);
        Customer newCustomer = customerRepository
                .save(new Customer(customerRequest.getName(), customerRequest.getSurname(), customerRequest.getEmail(), LocalDateTime.now(),
                        CustomerStatus.ACTIVE, customerRequest.getAddress()
                        , ControllerHelper.getHashedPassword(customerRequest.getPassword()),
                        //deniz@example.com is special case for test purpose, all other users are added as simple users
                        customerRequest.getEmail().equals("deniz@example.com")?CustomerRole.ROLE_ADMIN:CustomerRole.ROLE_USER));
        changeLogSaver.saveLog(newCustomer, newCustomer, newCustomer.getEmail());
        return new ResponseEntity<>(new ObjectResponse<>(newCustomer), HttpStatus.CREATED);
    }
}
