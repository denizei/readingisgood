package com.getir.readingisgood.model.request;

import com.getir.readingisgood.domain.OrderStatus;

import javax.validation.constraints.NotNull;

public class OrderStatusRequest {
    @NotNull
    private OrderStatus orderStatus;

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
