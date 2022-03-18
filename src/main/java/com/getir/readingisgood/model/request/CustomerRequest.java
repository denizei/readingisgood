package com.getir.readingisgood.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

public class CustomerRequest {
    @Size(min = 0, max = 40, message = "Name ")
    private String name;
    @Size(min = 0, max = 50, message = "Surname ")
    private String surname;
    @Size(min = 0, max = 50, message = "E-mail ")
    private String email;
    @Size(min = 0, max = 100, message = "Address ")
    private String address;
    @Size(min = 6, max = 40, message = "Password ")
    private String password;

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
}
