package com.getir.readingisgood.model.request;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OrderBookRequest {
    @NotNull
    private Long bookId;

    @NotNull
    @Min(value = 1, message = "Book ")
    private Integer quantity;

    public OrderBookRequest(){

    }
    public OrderBookRequest(Long bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderBookRequest{" +
                "bookId=" + bookId +
                ", quantity=" + quantity +
                '}';
    }
}
