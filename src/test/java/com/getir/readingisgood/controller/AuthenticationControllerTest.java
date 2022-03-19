package com.getir.readingisgood.controller;

import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.domain.CustomerRole;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest extends AbstractTest {
    Logger logger = LoggerFactory.getLogger(AuthenticationControllerTest.class);

    @MockBean
    CustomerRepository customerRepository;

    @BeforeEach
    public void setUp() {
        logger.info("Setting up AuthenticationControllerTest");
        super.setUp();

        Customer admin = new Customer();
        admin.setId(1l);
        admin.setName("Admin");
        admin.setSurname("sn");
        admin.setRole(CustomerRole.ROLE_ADMIN);
        admin.setPassword(ControllerHelper.getHashedPassword("123456"));
        admin.setEmail("adminperson@example.com");
        when(customerRepository.findByEmail("adminperson@example.com"))
                .thenReturn(Optional.of(admin));
    }

    @Test
    public void authorizeTest() throws Exception {
        String uri = "/authenticate";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .content("{\"email\":\"" + "adminperson@example.com" + "\",\"password\":\"" + "123456" + "\"}")
                .header("content-type", "application/json")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());


        assertEquals(HttpStatus.OK.value(), status);
        assertNotNull(jsonObject.getString("token"));
        assertEquals(true, jsonObject.isNull("error"));
    }

    @Test
    public void authorizeWrongCredentialsTest() throws Exception {
        String uri = "/authenticate";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .content("{\"email\":\"wrongperson@example.com\",\"password\":\"123456\"}")
                        .header("content-type", "application/json")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());

        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        assertEquals(jsonObject.getInt("status"), 400);
    }

    @Test
    public void authorizeWrongPasswordCredentialsTest() throws Exception {
        String uri = "/authenticate";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .content("{\"email\":\"adminperson@example.com\",\"password\":\"123432\"}")
                        .header("content-type", "application/json")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());

        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        assertEquals(jsonObject.getInt("status"), 400);
    }

    @Test
    public void authorizeInvalidCredentialsTest() throws Exception {
        String uri = "/authenticate";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .content("{\"email\":\"adminperson@example.com\",\"password\":\"1232\"}")
                        .header("content-type", "application/json")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());

        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        assertEquals(jsonObject.getInt("status"), 400);
    }


}

