package com.getir.readingisgood.model;

import com.getir.readingisgood.domain.Customer;

public class OrderCustomerResponse {
    private Long id;
    private String name;
    private String email;
    private String address;

    public OrderCustomerResponse() {

    }

    public OrderCustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.name = (customer.getName() + " " + customer.getSurname()).trim();
        this.email = customer.getEmail();
        this.address = customer.getAddress();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
