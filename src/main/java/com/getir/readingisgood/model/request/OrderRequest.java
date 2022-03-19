package com.getir.readingisgood.model.request;

import javax.validation.constraints.NotNull;
import java.util.List;

public class OrderRequest {

    @NotNull
    private List<OrderBookRequest> books;

    public List<OrderBookRequest> getBooks() {
        return books;
    }

    public void setBooks(List<OrderBookRequest> books) {
        this.books = books;
    }

}
