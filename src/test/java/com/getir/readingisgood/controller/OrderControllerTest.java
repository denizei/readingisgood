package com.getir.readingisgood.controller;

import com.getir.readingisgood.domain.*;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.model.request.OrderBookRequest;
import com.getir.readingisgood.model.request.OrderRequest;
import com.getir.readingisgood.model.request.OrderStatusRequest;
import com.getir.readingisgood.repository.BookRepository;
import com.getir.readingisgood.repository.CustomerRepository;
import com.getir.readingisgood.repository.OrderBookRepository;
import com.getir.readingisgood.repository.OrderRepository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderControllerTest extends AbstractTest {
    Logger logger = LoggerFactory.getLogger(OrderControllerTest.class);

    @Autowired
    BookRepository bookRepository;
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderBookRepository orderBookRepository;

    @Autowired
    CustomerRepository customerRepository;

    Long orderId = null;
    com.getir.readingisgood.domain.Order order;

    Long book1Id = null;
    com.getir.readingisgood.domain.Book book1;
    Long book2Id = null;
    com.getir.readingisgood.domain.Book book2;

    Long adminId = null;
    Customer admin;
    Long userId = null;
    Customer user;

    @BeforeEach
    public void setUp() {
        logger.info("BookControllerTest setting  up");
        super.setUp();
        getUser();
        getAdmin();
        getBook1();
        getBook2();
    }

    @Test
    @WithMockUser(username = "userperson@example.com", password = "123456", roles = "USER")
    @Order(0)
    public void createOrderTest() throws Exception {
        String uri = "/api/order";
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBooks(new ArrayList<>(Arrays.asList(
                new OrderBookRequest(getBook1().getId(), 4),
                new OrderBookRequest(getBook2().getId(), 2)
        )));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        assertEquals(true, jsonObject.isNull("error"));
        orderId = jsonObject.getJSONObject("data").getLong("id");
        logger.info(jsonObject.toString());
        assertEquals(200, jsonObject.getInt("status"));
        assertEquals(2, jsonObject.getJSONObject("data").getJSONArray("books").length());
        assertEquals(true, jsonObject.isNull("error"));

    }

    @Test
    @WithMockUser(username = "userperson@example.com", password = "123456", roles = "USER")
    @Order(0)
    public void createOrderConstraintViolationTest() throws Exception {
        String uri = "/api/order";
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setBooks(new ArrayList<>(Arrays.asList(
                new OrderBookRequest(getBook1().getId(), -4),
                new OrderBookRequest(getBook2().getId(), 2)
        )));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY.getCode());

    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(1)
    public void updateOrderTest() throws Exception {

        getSavedOrder();

        String uri = "/api/order/changestatus/" + order.getId();

        OrderStatusRequest updateRequest = new OrderStatusRequest();

        updateRequest.setOrderStatus(OrderStatus.PREPARING);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 200);
        assertEquals(true, jsonObject.isNull("error"));
        assertEquals(jsonObject.getJSONObject("data").getString("status"), OrderStatus.PREPARING.name());
    }


    @Test
    @WithMockUser(username = "userperson@example.com", password = "123456", roles = "USER")
    @Order(1)
    public void updateOrderInvalidAuthorizationLevelTest() throws Exception {

        getSavedOrder();

        String uri = "/api/order/changestatus/" + order.getId();

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType("application/json")
                        .content("{\"orderStatus\": \"PREPARING\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.USER_IS_NOT_AUTHORIZED.getCode());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(1)
    public void updateOrderInvalidUpdateMessageTest() throws Exception {

        getSavedOrder();

        String uri = "/api/order/changestatus/" + order.getId();

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType("application/json")
                        .content("{\"orderStatus\": \"PRYPARING\"}")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY.getCode());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(1)
    public void updateOrderWithErrorTest() throws Exception {

        getSavedOrder();

        String uri = "/api/order/changestatus/10000";

        OrderStatusRequest updateRequest = new OrderStatusRequest();

        updateRequest.setOrderStatus(OrderStatus.PREPARING);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.ORDER_NOT_FOUND.getCode());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(2)
    public void getOrderByIdValidTest() throws Exception {

        getSavedOrder();

        String uri = "/api/order/" + order.getId();

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(jsonObject.getInt("status"), 200);
        assertEquals(true, jsonObject.isNull("error"));
        assertEquals(jsonObject.getJSONObject("data").getInt("id"), order.getId());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(3)
    public void getOrderByIdInvalidTest() throws Exception {
        String uri = "/api/order/asdfg";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), status);
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getInt("code"),
                GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY.getCode());
    }

    @Test
    @WithMockUser(username = "someperson@example.com", password = "123456", roles = "USER")
    @Order(4)
    public void getOrderByIdBadRequestTest() throws Exception {
        getSavedOrder();

        String uri = "/api/order/" + order.getId();

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));

    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(4)
    public void getOrderByIdNotFoundTest() throws Exception {
        String uri = "/api/order/12345";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(400, jsonObject.getInt("status"));
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getInt("code"),
                GeneralException.ErrorCode.ORDER_NOT_FOUND.getCode());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(5)
    public void getOrdersWithNoDataTest() throws Exception {

        getSavedOrder();

        String uri = "/api/orders";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(0, jsonObject.getJSONArray("data").length());
    }

    @Test
    @WithMockUser(username = "userperson@example.com", password = "123456", roles = "USER")
    @Order(5)
    public void getOrdersWithDataTest() throws Exception {

        getSavedOrder();

        String uri = "/api/orders";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(1, jsonObject.getJSONArray("data").length());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(5)
    public void getOrdersTest() throws Exception {

        getSavedOrder();

        String uri = "/api/order?startDate=2020-01-01&endDate=2023-01-01";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(1, jsonObject.getJSONArray("data").length());
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(5)
    public void getOrdersWithPaginationTest() throws Exception {

        getSavedOrder();

        String uri = "/api/order?startDate=2020-01-01&endDate=2023-01-01&limit=3&page=5";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(0, jsonObject.getJSONArray("data").length());
    }

    private void getSavedOrder() {
        System.out.println("order = " + orderId);
        if (orderId == null) {
            order = new com.getir.readingisgood.domain.Order();
            order.setStatus(OrderStatus.ORDER_TAKEN);
            order.setOrderDate(LocalDateTime.now());
            order.setCustomer(getUser());

            OrderBook b1 = new OrderBook();
            b1.setOrder(order);
            b1.setBook(getBook1());
            b1.setPrice(b1.getBook().getPrice() * 4);
            b1.setQuantity(4);

            OrderBook b2 = new OrderBook();
            b2.setOrder(order);
            b2.setBook(getBook2());
            b2.setPrice(getBook2().getPrice() * 2);
            b2.setQuantity(2);

            order.setBooks(new ArrayList<>(Arrays.asList(
                    b1, b2
            )));
            try {
                order = orderRepository.save(order);
                orderId = order.getId();
            } catch (Exception ex) {

            }
        } else
            order = orderRepository.findById(orderId).get();
    }


    private Book getBook1() {
        if (book1Id == null) {
            book1 = new Book();
            book1.setAuthor("Carl Sagan");
            book1.setName("Cosmos");
            book1.setStockCount(22L);
            book1.setPrice(22D);
            book1.setPublicationYear(1965);
            book1.setIsbn("1234562923456");
            book1 = bookRepository.save(book1);
            book1Id = book1.getId();
            bookRepository.flush();
        } else
            book1 = bookRepository.findById(book1Id).get();
        return book1;
    }

    private Book getBook2() {
        if (book2Id == null) {
            book2 = new Book();
            book2.setAuthor("George Orwell");
            book2.setName("Animal Farm");
            book2.setStockCount(14L);
            book2.setPrice(7D);
            book2.setPublicationYear(1935);
            book2.setIsbn("9934567123466");
            book2 = bookRepository.save(book2);
            book2Id = book2.getId();
            bookRepository.flush();
        } else
            book2 = bookRepository.findById(book2Id).get();
        return book2;
    }

    private Customer getUser() {
        if (userId == null) {
            user = new Customer();
            user.setName("User");
            user.setPassword(ControllerHelper.getHashedPassword("123456"));
            user.setSurname("sn");
            user.setRegistrationDate(LocalDateTime.now());
            user.setAddress("NA");
            user.setEmail("userperson@example.com");
            user.setStatus(CustomerStatus.ACTIVE);
            user.setRole(CustomerRole.ROLE_USER);
            user = customerRepository.save(user);
            customerRepository.flush();
            userId = user.getId();
        } else
            user = customerRepository.findById(userId).get();
        return user;
    }

    private Customer getAdmin() {
        if (adminId == null) {
            admin = new Customer();
            admin.setName("Admin");
            admin.setSurname("sn");
            admin.setPassword(ControllerHelper.getHashedPassword("123456"));
            admin.setEmail("adminperson@example.com");
            admin.setRegistrationDate(LocalDateTime.now());
            admin.setAddress("NA");
            admin.setStatus(CustomerStatus.ACTIVE);
            admin.setRole(CustomerRole.ROLE_ADMIN);
            admin = customerRepository.save(admin);
            customerRepository.flush();
            adminId = admin.getId();
        } else
            admin = customerRepository.findById(adminId).get();
        return admin;
    }
}
