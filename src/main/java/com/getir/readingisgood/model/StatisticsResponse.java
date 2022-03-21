package com.getir.readingisgood.model;

public class StatisticsResponse {
    private Integer month;
    private Integer year;
    private Integer totalOrderCount;
    private Integer totalBookCount;
    private Double totalPurchasedAmount;

    public StatisticsResponse(int year, int month, Number[] values) {
        this.year = year;
        this.month = month;
        this.totalOrderCount = ((Number) values[0]).intValue();
        this.totalPurchasedAmount = ((Number) values[1]).doubleValue();
        this.totalBookCount = ((Number) values[2]).intValue();

    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Integer totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public Integer getTotalBookCount() {
        return totalBookCount;
    }

    public void setTotalBookCount(Integer totalBookCount) {
        this.totalBookCount = totalBookCount;
    }

    public Double getTotalPurchasedAmount() {
        return totalPurchasedAmount;
    }

    public void setTotalPurchasedAmount(Double totalPurchasedAmount) {
        this.totalPurchasedAmount = totalPurchasedAmount;
    }
}
