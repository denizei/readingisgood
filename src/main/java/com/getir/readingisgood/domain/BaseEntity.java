package com.getir.readingisgood.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public interface BaseEntity {


    public Long getId();

    public void setId(Long id);
}
