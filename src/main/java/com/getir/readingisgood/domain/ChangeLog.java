package com.getir.readingisgood.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CHANGE_LOG")
public class ChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="customer_id",nullable = false)
    private Customer customer;
    @Column(name="entity_id",nullable = false)
    private Long entityId;
    @Column(name="action_date",nullable = false)
    private LocalDateTime actionDate;
    @Column(name="status",nullable = false)
    private ChangeLogActionType actionType;
    @Column(name="old_value")
    private String oldValue;
    @Column(name="new_value",nullable = false)
    private String newValue;

    public ChangeLog() {
    }

    public ChangeLog(Customer customer, Long entityId, ChangeLogActionType actionType, String oldValue, String newValue) {
        this.customer = customer;
        this.entityId = entityId;
        this.actionType = actionType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.actionDate=LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }

    public ChangeLogActionType getActionType() {
        return actionType;
    }

    public void setActionType(ChangeLogActionType actionType) {
        this.actionType = actionType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
