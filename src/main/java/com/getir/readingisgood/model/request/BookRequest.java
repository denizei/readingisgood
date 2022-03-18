package com.getir.readingisgood.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class BookRequest {
    @NotNull
    private String name;
    @NotNull
    private String author;
    @NotNull
    @Min(value = 0, message = "Price")
    private Double price;
    @NotNull
    private Integer publicationYear;
    @NotNull
    private String isbn;
    @NotNull
    @Min(value = 0, message = "Stock")
    private Long stockCount;

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

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getStockCount() {
        return stockCount;
    }

    public void setStockCount(Long stockCount) {
        this.stockCount = stockCount;
    }
}
