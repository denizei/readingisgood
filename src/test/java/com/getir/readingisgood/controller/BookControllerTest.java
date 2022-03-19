package com.getir.readingisgood.controller;

import com.getir.readingisgood.domain.Book;
import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.model.request.BookRequest;
import com.getir.readingisgood.model.request.BookUpdateRequest;
import com.getir.readingisgood.repository.BookRepository;
import com.getir.readingisgood.repository.CustomerRepository;
import org.junit.jupiter.api.*;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;


@AutoConfigureJsonTesters
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookControllerTest extends AbstractTest {
    Logger logger = LoggerFactory.getLogger(BookControllerTest.class);


    @Autowired
    BookRepository bookRepository;

    @MockBean
    CustomerRepository customerRepository;

    Long bookId = null;
    Book book;


    @BeforeEach
    public void setUp() {
        logger.info("BookControllerTest setting  up");
        super.setUp();
        Customer admin = new Customer();
        admin.setId(1l);
        admin.setName("Admin");
        admin.setSurname("sn");
        admin.setEmail("adminperson@example.com");
        when(customerRepository.findByEmail("adminperson@example.com"))
                .thenReturn(Optional.of(admin));
        Customer user = new Customer();
        user.setId(2l);
        user.setName("User");
        user.setSurname("sn");
        user.setEmail("userperson@example.com");
        when(customerRepository.findByEmail("userperson@example.com"))
                .thenReturn(Optional.of(user));
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(0)
    public void createBookTest() throws Exception {
        String uri = "/api/book";
        BookRequest bookRequest = new BookRequest();
        bookRequest.setAuthor("Frank Herbert");
        bookRequest.setName("Dune");
        bookRequest.setStockCount(12L);
        bookRequest.setPrice(12D);
        bookRequest.setPublicationYear(1965);
        bookRequest.setIsbn("1234567123456");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 200);
        assertEquals(true, jsonObject.isNull("error"));
        assertEquals(jsonObject.getJSONObject("data").getString("isbn"), "1234567123456");
        assertEquals(jsonObject.getJSONObject("data").getString("name"), "Dune");
        assertEquals(jsonObject.getJSONObject("data").getLong("stockCount"), 12L);
        bookId = jsonObject.getJSONObject("data").getLong("id");


        bookRequest = new BookRequest();
        bookRequest.setAuthor("Frank Herbert");
        bookRequest.setName("Dune 2");
        bookRequest.setStockCount(12L);
        bookRequest.setPrice(24D);
        bookRequest.setPublicationYear(1965);
        bookRequest.setIsbn("1234567123456");

        mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getInt("code"),
                GeneralException.ErrorCode.DUPLICATE_BOOK.getCode());
    }

    @Test
    @WithMockUser(username = "userperson@example.com", password = "123456", roles = "USER")
    @Order(0)
    public void createBookAuthorizedFailureTest() throws Exception {
        String uri = "/api/book";
        BookRequest bookRequest = new BookRequest();
        bookRequest.setAuthor("Frank Herbert");
        bookRequest.setName("Dune");
        bookRequest.setStockCount(12L);
        bookRequest.setPrice(12D);
        bookRequest.setPublicationYear(1965);
        bookRequest.setIsbn("1234567123456");

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
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
    public void updateBookTest() throws Exception {

        getSavedBook();

        String uri = "/api/book/" + book.getId();

        BookUpdateRequest bookRequest = new BookUpdateRequest();

        bookRequest.setPrice(25D);
        bookRequest.setStockCount(111L);
        logger.info(objectMapper.writeValueAsString(bookRequest));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 200);
        assertEquals(true, jsonObject.isNull("error"));
        assertEquals(jsonObject.getJSONObject("data").getString("isbn"), "1234567123456");
        assertEquals(jsonObject.getJSONObject("data").getDouble("price"), 25D);
        assertEquals(jsonObject.getJSONObject("data").getLong("stockCount"), 111L);
    }

    @Test
    @WithMockUser(username = "adminperson@example.com", password = "123456", roles = "ADMIN")
    @Order(1)
    public void updateBookWithInvalidRequestTest() throws Exception {

        getSavedBook();

        String uri = "/api/book/" + bookId;

        BookUpdateRequest bookRequest = new BookUpdateRequest();

        bookRequest.setPrice(-1D);
        logger.info(objectMapper.writeValueAsString(bookRequest));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequest))
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
    public void updateBookWithErrorTest() throws Exception {

        getSavedBook();

        String uri = "/api/book/1000000";

        BookUpdateRequest bookRequest = new BookUpdateRequest();

        bookRequest.setPrice(25D);
        bookRequest.setStockCount(111L);
        logger.info(objectMapper.writeValueAsString(bookRequest));

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookRequest))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        logger.info(mvcResult.getResponse().getContentAsString());

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getLong("code")
                , GeneralException.ErrorCode.BOOK_NOT_FOUND.getCode());
    }

    @Test
    @Order(2)
    public void getBookByIdValidTest() throws Exception {

        getSavedBook();

        String uri = "/api/book/" + book.getId();

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(jsonObject.getInt("status"), 200);
        assertEquals(true, jsonObject.isNull("error"));
        assertEquals(jsonObject.getJSONObject("data").getInt("id"), book.getId());
        assertEquals(jsonObject.getJSONObject("data").getString("name"), "Dune");
    }

    @Test
    @Order(3)
    public void getBookByIdInvalidTest() throws Exception {
        String uri = "/api/book/asdfg";

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
    @Order(4)
    public void getBookByIdNotFoundTest() throws Exception {
        String uri = "/api/book/12345";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        assertEquals(HttpStatus.NOT_FOUND.value(), status);
        assertEquals(jsonObject.getInt("status"), 400);
        assertEquals(true, jsonObject.isNull("data"));
        assertEquals(jsonObject.getJSONObject("error").getInt("code"),
                GeneralException.ErrorCode.BOOK_NOT_FOUND.getCode());
    }

    @Test
    @Order(5)
    public void getAllBooksTest() throws Exception {

        getSavedBook();

        String uri = "/api/book";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        logger.info(jsonObject.toString());
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        assertEquals(1, jsonObject.getJSONArray("data").length());
    }

    private void getSavedBook() {
        System.out.println("book = " + bookId);
        if (bookId == null) {
            book = new Book();
            book.setAuthor("Frank Herbert");
            book.setName("Dune");
            book.setStockCount(12L);
            book.setPrice(12D);
            book.setPublicationYear(1965);
            book.setIsbn("1234567123456");
            try {
                book = bookRepository.save(book);
                bookId = book.getId();
            } catch (Exception ex) {

            }
        } else
            book = bookRepository.findById(bookId).get();
    }

}
