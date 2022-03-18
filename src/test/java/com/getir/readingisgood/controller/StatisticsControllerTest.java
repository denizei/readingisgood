package com.getir.readingisgood.controller;

import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.domain.CustomerRole;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.repository.CustomerRepository;
import com.getir.readingisgood.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        Customer admin=new Customer();
        admin.setId(1l);
        admin.setName("Admin");
        admin.setSurname("sn");
        admin.setRole(CustomerRole.ROLE_ADMIN);
        admin.setPassword(ControllerHelper.getHashedPassword("123456"));
        admin.setEmail("adminperson@example.com");
        when(customerRepository.findByEmail("adminperson@example.com"))
                .thenReturn(Optional.of(admin));

        when( orderRepository.getStatsBetweenMonths(new Date(1577826000000L), new Date(1672520400000l)))
                .thenReturn(new ArrayList<>(Arrays.asList(
                        new Object[]{2022,2,4,252.24,12}
                        ,new Object[]{2022,3,6,262.27,13})));

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
        assertEquals(2,jsonObject.getJSONArray("data").length());
    }
}

