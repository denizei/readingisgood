package com.getir.readingisgood.controller;

import com.getir.readingisgood.model.request.CustomerRequest;
import com.getir.readingisgood.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTest extends AbstractTest {
    Logger logger = LoggerFactory.getLogger(CustomerControllerTest.class);

    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    public void createCustomerTest() throws Exception {
        customerRepository.deleteAll();
        String uri = "/api/customer";

        CustomerRequest customer = new CustomerRequest();
        customer.setEmail("samplecustomer@example.com");
        customer.setPassword("123456");
        customer.setAddress("Ankara Çankaya 123A");
        customer.setName("Sample");
        customer.setSurname("Person");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .header("content-type", "application/json")
                        .content(objectMapper.writeValueAsString(customer))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.CREATED.value(), status);
        logger.info(jsonObject.toString());
        assertEquals(true, jsonObject.isNull("error"));
        assertEquals(jsonObject.getJSONObject("data").getString("email"), "samplecustomer@example.com");


    }

    @Test
    public void createCustomerUserAlreadyExistsTest() throws Exception {
        String uri = "/api/customer";

        CustomerRequest customer = new CustomerRequest();
        customer.setEmail("customer1@example.com");
        customer.setPassword("123456");
        customer.setAddress("Ankara Çankaya 123A");
        customer.setName("Customer 1");
        customer.setSurname("Person");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .header("content-type", "application/json")
                        .content(objectMapper.writeValueAsString(customer))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());
        MvcResult mvcResult2 = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .header("content-type", "application/json")
                        .content(objectMapper.writeValueAsString(customer))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult2.getResponse().getContentAsString());
        logger.info(mvcResult2.getResponse().getContentAsString());

        int status = mvcResult2.getResponse().getStatus();
        assertEquals(HttpStatus.ALREADY_REPORTED.value(), status);
        assertEquals(true, jsonObject.isNull("data"));

    }

}
