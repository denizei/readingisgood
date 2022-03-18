package com.getir.readingisgood.model;

public class StatisticsResponse {
    private Integer month;
    private Integer year;
    private Integer totalOrderCount;
    private Integer totalBookCount;
    private Double totalPurchasedAmount;

    public StatisticsResponse() {
    }

    public StatisticsResponse(Object[] object) {
        this.year =((Number) object[0]).intValue();
        this.month =((Number) object[1]).intValue();
        this.totalOrderCount =((Number) object[2]).intValue();
        this.totalPurchasedAmount = ((Number) object[3]).doubleValue();
        this.totalBookCount = ((Number) object[4]).intValue();

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
