package com.getir.readingisgood.controller;

import com.getir.readingisgood.controller.advice.ChangeLogSaver;
import com.getir.readingisgood.domain.*;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.ObjectResponse;
import com.getir.readingisgood.model.OrderBookResponse;
import com.getir.readingisgood.model.OrderResponse;
import com.getir.readingisgood.model.QueryObjectResponse;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.model.request.OrderBookRequest;
import com.getir.readingisgood.model.request.OrderRequest;
import com.getir.readingisgood.model.request.OrderStatusRequest;
import com.getir.readingisgood.repository.BookRepository;
import com.getir.readingisgood.repository.CustomerRepository;
import com.getir.readingisgood.repository.OrderBookRepository;
import com.getir.readingisgood.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Validated
@CrossOrigin(origins = "http://localhost:8082")
@RestController
@Tag(name = "Order Operations", description = "The operations on orders")
@RequestMapping("/api")
public class OrderController {
    Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    BookRepository bookRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrderBookRepository orderBookRepository;
    @Autowired
    ChangeLogSaver changeLogSaver;

    @PostMapping("/order")
    @Transactional
    @Operation(summary = "Creates an order in the database", description = "Any authorized user can perform this operation <br/>" +
            "The system generates an error when book id is not found or book stock is not available at that moment")
    @ApiResponse(responseCode = "200", description = "Order taken")
    @ApiResponse(responseCode = "400", description = "Request is not proper")
    public ResponseEntity<ObjectResponse<OrderResponse>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            UserDetails userDetails =
                    (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Optional<Customer> customer = customerRepository.findByEmail(userDetails.getUsername());

            if (customer.isEmpty()) {
                throw new GeneralException(GeneralException.ErrorCode.CUSTOMER_NOT_FOUND
                        , " Customer not found ");
            }
            Order order = new Order();
            order.setCustomer(customer.get());
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.ORDER_TAKEN);
            List<OrderBook> orderBooks = new ArrayList<>();
            List<Book> books = new ArrayList<>();
            List<ChangeLog> bookChanges = new ArrayList<>();
            int totalBooks = 0;
            double totalPrice = 0;
            for (OrderBookRequest obr : orderRequest.getBooks()) {

                Optional<Book> book = bookRepository.findById(obr.getBookId());
                if (book.isEmpty()) {
                    throw new GeneralException(GeneralException.ErrorCode.BOOK_NOT_FOUND
                            , " Book not found: " + obr.getBookId());
                }
                OrderBook ob = new OrderBook();
                ob.setOrder(order);
                if (book.get().getStockCount() < obr.getQuantity()) {
                    throw new GeneralException(GeneralException.ErrorCode.BOOK_NOT_ENOUGH_QUANTITY
                            , " Book quantity is less than available: " + obr.getBookId() + ":" + obr.getQuantity());
                }
                ob.setQuantity(obr.getQuantity());
                ob.setBook(book.get());
                ob.setPrice(book.get().getPrice() * obr.getQuantity());
                orderBooks.add(ob);
                long newStock = book.get().getStockCount() - obr.getQuantity();
                bookChanges.add(new ChangeLog(order.getCustomer(), book.get().getId(), ChangeLogActionType.BOOK_UPDATE_BY_ORDER
                        , "stock: " + book.get().getStockCount(), "stock: " + newStock));
                book.get().setStockCount(newStock);
                books.add(book.get());
                totalBooks += obr.getQuantity();
                totalPrice += ob.getPrice();
            }
            order = orderRepository.save(order);
            orderBookRepository.saveAll(orderBooks);
            bookRepository.saveAll(books);
            OrderResponse orderResponse = new OrderResponse(order);
            orderBooks.forEach(ob -> orderResponse.getBooks().add(new OrderBookResponse(ob)));
            changeLogSaver.saveLog(order, order.getCustomer(), "books: " + totalBooks + ", price: " + totalPrice);
            changeLogSaver.saveLogs(bookChanges);
            return new ResponseEntity<>(new ObjectResponse<>(orderResponse), HttpStatus.CREATED);
        } catch (GeneralException e) {
            logger.error("", e);
            return new ResponseEntity<>(new ObjectResponse<>(e), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get Order Details By Order Id", description = "ROLE_ADMIN can see all orders, ROLE_USER can see their order <br/>" +
            "If the customer has ROLE_USER role, they can only see the order if it is placed by them")
    @Parameter(name = "id", description = "Order id to search", required = true)
    @ApiResponse(responseCode = "200", description = "Order found")
    @GetMapping("/order/{id}")
    public ResponseEntity<QueryObjectResponse<OrderResponse>> getOrderByKey(@PathVariable("id") long id) {
        try {
            Optional<Order> order = orderRepository.findById(id);
            if (order.isPresent()) {
                UserDetails userDetails =
                        (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (userDetails.getAuthorities().stream().filter(a -> a.getAuthority().equals("ROLE_ADMIN")).count() == 0) {
                    if (!userDetails.getUsername().equals(order.get().getCustomer().getEmail()))
                        return ResponseEntity.badRequest().body(new QueryObjectResponse<>(
                                new GeneralException(GeneralException.ErrorCode.CUSTOMER_NOT_FOUND, "User is not authenticated")
                                , ControllerHelper.getQueryMap("id", id)));
                }
                return new ResponseEntity<>(new QueryObjectResponse<>(new OrderResponse(order.get())
                        , ControllerHelper.getQueryMap("id", id)), HttpStatus.OK);
            } else {
                throw new GeneralException(GeneralException.ErrorCode.ORDER_NOT_FOUND
                        , " Order not found: " + id);
            }
        } catch (GeneralException e) {
            logger.error("", e);
            return new ResponseEntity<>(new QueryObjectResponse<>(e
                    , ControllerHelper.getQueryMap("id", id)), HttpStatus.BAD_REQUEST);
        }

    }

    @Operation(summary = "Get Order Details By Date Interval", description = "User with ROLE_ADMIN role can use this function")
    @Parameter(name = "startDate", description = "Start Date of Interval", required = true)
    @Parameter(name = "endDate", description = "End Date of Interval", required = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/order")
    public ResponseEntity<QueryObjectResponse<List<OrderResponse>>> queryOrder(
            @RequestParam(value = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @Valid @RequestParam(value = "limit", required = false, defaultValue = "20")
            @Min(value = 1, message = "Limit cannot be lower than 1") Integer limit,
            @RequestParam(value = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "Page cannot be lower than 0") Integer page) {


        List<Order> orders = orderRepository.getAllBetweenDates(startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                , endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), PageRequest.of(page, limit));

        return new ResponseEntity<>(new QueryObjectResponse<>(orders.stream().map(OrderResponse::new).collect(Collectors.toList())
                , ControllerHelper.getQueryMap("startDate", startDate, "endDate", endDate, "page", page, "limit", limit))
                , HttpStatus.OK);


    }

    @Operation(summary = "Get All Orders of a customer with Pagination", description = "All users can use this function to query their orders")
    @GetMapping("/orders")
    public ResponseEntity<QueryObjectResponse<List<OrderResponse>>> queryOrders(
            @Valid @RequestParam(value = "limit", required = false, defaultValue = "20")
            @Min(value = 1, message = "Limit cannot be lower than 1") Integer limit,
            @RequestParam(value = "page", required = false, defaultValue = "0")
            @Min(value = 0, message = "Page cannot be lower than 0") Integer page) {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<Customer> customer = customerRepository.findByEmail(userDetails.getUsername());
        List<Order> orders = orderRepository.findByCustomer(customer.get(), PageRequest.of(page, limit));
        return new ResponseEntity<>(new QueryObjectResponse<>(orders.stream().map(OrderResponse::new).collect(Collectors.toList())
                , ControllerHelper.getQueryMap("page", page, "limit", limit))
                , HttpStatus.OK);

    }

    @Operation(summary = "Update order status", description = "Users with role ROLE_ADMIN can use this function")
    @Parameter(name = "id", description = "Order Id", required = true)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/order/changestatus/{id}")
    public ResponseEntity<ObjectResponse<OrderResponse>> updateOrderStatus(@PathVariable("id") long id, @Valid @RequestBody OrderStatusRequest orderStatusRequest) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            Order updatedOrder = order.get();
            String oldStatus = updatedOrder.getStatus().name();
            if (orderStatusRequest.getOrderStatus() != null) {
                updatedOrder.setStatus(orderStatusRequest.getOrderStatus());
            }
            updatedOrder = orderRepository.save(updatedOrder);
            changeLogSaver.saveLog(updatedOrder, oldStatus, orderStatusRequest.getOrderStatus().name());
            return new ResponseEntity<>(new ObjectResponse<>(new OrderResponse(updatedOrder)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ObjectResponse<>(
                    new GeneralException(GeneralException.ErrorCode.ORDER_NOT_FOUND, id + " is not found"))
                    , HttpStatus.NOT_FOUND);
        }
    }

}
