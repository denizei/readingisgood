package com.getir.readingisgood.model;


import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879123456844L;
    private final String jwttoken;

    public JwtResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public String getToken() {
        return this.jwttoken;
    }
}