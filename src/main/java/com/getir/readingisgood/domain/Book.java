package com.getir.readingisgood.domain;

import javax.persistence.*;

@Entity
@Table(name = "BOOK")
public class Book implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @Column(name = "name", length = 60, nullable = false)
    private String name;
    @Column(name = "author", length = 50, nullable = false)
    private String author;
    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "publication_year", nullable = false)
    private Integer publicationYear;
    @Column(name = "isbn", length = 13, nullable = false, unique = true)
    private String isbn;
    @Column(name = "stock_count", nullable = false)
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
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
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
