package com.getir.readingisgood.model.request;


import javax.validation.constraints.Min;

public class BookUpdateRequest {

    @Min(value = 0, message = "Price")
    private Double price;

    @Min(value = 0, message = "Stock")
    private Long stockCount;


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
}
