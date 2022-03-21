package com.getir.readingisgood.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("customer")
public class Customer implements BaseEntity {
    @Id
    private String id;
    private String name;
    private String surname;
    private String email;
    private LocalDateTime registrationDate;
    private CustomerStatus status;
    private String address;
    private String password;
    private CustomerRole role;

    public Customer() {
    }

    public Customer(String name, String surname, String email, LocalDateTime registrationDate,
                    CustomerStatus status, String address, String password, CustomerRole role) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.registrationDate = registrationDate;
        this.status = status;
        this.address = address;
        this.password = password;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public CustomerRole getRole() {
        return role;
    }

    public void setRole(CustomerRole role) {
        this.role = role;
    }
}
