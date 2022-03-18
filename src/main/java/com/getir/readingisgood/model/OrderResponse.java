package com.getir.readingisgood.model;

import com.getir.readingisgood.domain.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {
    private Long id;
    private OrderCustomerResponse customer;
    private String status;
    private LocalDateTime time;
    private List<OrderBookResponse> books = new ArrayList<>();

    public OrderResponse() {

    }

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.customer = new OrderCustomerResponse(order.getCustomer());
        this.status = order.getStatus().name();
        this.time = order.getOrderDate();
        order.getBooks().forEach(orderBook -> books.add(new OrderBookResponse(orderBook)));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderCustomerResponse getCustomer() {
        return customer;
    }

    public void setCustomer(OrderCustomerResponse customer) {
        this.customer = customer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public List<OrderBookResponse> getBooks() {
        return books;
    }

    public void setBooks(List<OrderBookResponse> books) {
        this.books = books;
    }
}
