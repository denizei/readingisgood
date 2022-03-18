package com.getir.readingisgood.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMER_ORDER")
public class Order implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="customer_id")
    private Customer customer;
    @Column(name="order_date")
    private LocalDateTime orderDate;
    @Column(name="status")
    private OrderStatus status;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderBook> books = new ArrayList<>();

    public Order() {
    }

    public Order(Customer customer, LocalDateTime orderDate, OrderStatus status) {
        this.customer = customer;
        this.orderDate = orderDate;
        this.status = status;
    }
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderBook> getBooks() {
        return books;
    }

    public void setBooks(List<OrderBook> books) {
        this.books = books;
    }
}
