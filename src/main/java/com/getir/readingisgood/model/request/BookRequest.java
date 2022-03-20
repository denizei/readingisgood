package com.getir.readingisgood.model.request;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BookRequest {
    @Size(max = 60, min = 1)
    @NotNull
    private String name;
    @Size(max = 50, min = 1)
    @NotNull
    private String author;
    @NotNull
    @Min(value = 0, message = "Price")
    private Double price;
    @NotNull
    private Integer publicationYear;
    @Size(min = 13)
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
