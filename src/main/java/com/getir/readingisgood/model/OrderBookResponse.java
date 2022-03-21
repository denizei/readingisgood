package com.getir.readingisgood.model;

import com.getir.readingisgood.domain.OrderBook;

public class OrderBookResponse {

    private String id;
    private String name;
    private String author;
    private Double price;
    private Integer quantity;

    public OrderBookResponse() {

    }

    public OrderBookResponse(OrderBook ob) {
        this.id = ob.getBook().getId();
        this.name = ob.getBook().getName();
        this.author = ob.getBook().getAuthor();
        this.price = ob.getPrice();
        this.quantity = ob.getQuantity();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
