package com.getir.readingisgood.controller;

import com.getir.readingisgood.domain.*;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.repository.CustomerRepository;
import com.getir.readingisgood.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest extends AbstractTest {
    Logger logger = LoggerFactory.getLogger(StatisticsControllerTest.class);

    @MockBean
    CustomerRepository customerRepository;

    @MockBean
    OrderRepository orderRepository;


    @BeforeEach
    public void setUp() {
        logger.info("Setting up AuthenticationControllerTest");
        super.setUp();

        Customer admin = new Customer();
        admin.setId("6237b1f1f50b9293944dd4d4");
        admin.setName("Admin");
        admin.setSurname("sn");
        admin.setRole(CustomerRole.ROLE_ADMIN);
        admin.setPassword(ControllerHelper.getHashedPassword("123456"));
        admin.setEmail("adminperson@example.com");
        when(customerRepository.findByEmail("adminperson@example.com"))
                .thenReturn(Optional.of(admin));

        Order order = new Order();
        order.setOrderDate(LocalDateTime.of(2022, 03, 20, 2, 2));
        order.setCustomer(admin);
        order.setStatus(OrderStatus.ORDER_TAKEN);
        new OrderBook(new Book(), 2, 25D);
        order.setBooks(new ArrayList<>(Arrays.asList(new OrderBook(new Book(), 2, 25D)
                , new OrderBook(new Book(), 5, 55D))));
        when(orderRepository.getStatsBetweenMonths(new Date(1577826000000L), new Date(1672520400000l)))
                .thenReturn(new ArrayList<>(Arrays.asList(order)));

    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    public void invalidParametersTest() throws Exception {
        String uri = "/api/stats/monthly";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());

        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY.getCode());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    public void wrongParametersTest() throws Exception {
        String uri = "/api/stats/monthly?startDate=today&endDate=2023-01";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());

        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY.getCode());
    }

    @Test
    @WithMockUser(username = "userperson@example.com", password = "123456", roles = "USER")
    public void invalidAuthorizedLevelTest() throws Exception {
        String uri = "/api/stats/monthly?startDate=2020-01&endDate=2023-01";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());

        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.USER_IS_NOT_AUTHORIZED.getCode());
    }


    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    public void succesfulStatTest() throws Exception {
        String uri = "/api/stats/monthly?startDate=2020-01&endDate=2023-01";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());

        assertEquals(jsonObject.getInt("status"), 200);
        assertEquals(true, jsonObject.isNull("error"));
        assertEquals(1, jsonObject.getJSONArray("data").length());
    }
}

