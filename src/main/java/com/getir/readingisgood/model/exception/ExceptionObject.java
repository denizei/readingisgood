package com.getir.readingisgood.model.exception;

import java.util.List;

public class ExceptionObject {

    private List<String> messages;
    private Integer code;

    public ExceptionObject(GeneralException exp) {
        this.messages = exp.getMessages();
        this.code = exp.getErrorCode().getCode();
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
