package com.getir.readingisgood.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("change_log")
public class ChangeLog {
    @Id
    private String id;
    private Customer customer;
    private String entityId;
    private LocalDateTime actionDate;
    private ChangeLogActionType actionType;
    private String oldValue;
    private String newValue;

    public ChangeLog() {
    }

    public ChangeLog(Customer customer, String entityId, ChangeLogActionType actionType, String oldValue, String newValue) {
        this.customer = customer;
        this.entityId = entityId;
        this.actionType = actionType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.actionDate = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
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
