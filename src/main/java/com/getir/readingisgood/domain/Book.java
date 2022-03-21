package com.getir.readingisgood.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("book")
public class Book implements BaseEntity {
    @Id
    protected String id;

    private String name;
    private String author;
    private Double price;
    private Integer publicationYear;
    private String isbn;
    private Long stockCount;

    public Book() {
    }

    public Book(String name, String author, Double price, Integer publicationYear, String isbn, Long stockCount) {
        this.name = name;
        this.author = author;
        this.price = price;
        this.publicationYear = publicationYear;
        this.isbn = isbn;
        this.stockCount = stockCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
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

    public Long getStockCount() {
        return stockCount;
    }

    public void setStockCount(Long stockCount) {
        this.stockCount = stockCount;
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
}
