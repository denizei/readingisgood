package com.getir.readingisgood.model;

import com.getir.readingisgood.model.exception.ExceptionObject;
import com.getir.readingisgood.model.exception.GeneralException;

public class ObjectResponse<T> {

    private Integer status;
    private T data;
    private ExceptionObject error;

    public ObjectResponse(T data) {
        this.status = 200;
        this.data = data;
    }

    public ObjectResponse(GeneralException exception) {
        this(400, exception);
    }

    public ObjectResponse(Integer status, GeneralException exception) {
        this.status = status;
        this.error = new ExceptionObject(exception);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ExceptionObject getError() {
        return error;
    }

    public void setError(ExceptionObject error) {
        this.error = error;
    }
}
