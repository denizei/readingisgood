package com.getir.readingisgood.model.exception;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

public class GeneralException extends Exception {
    public enum ErrorCode {
        ORDER_NOT_FOUND(6), DUPLICATE_BOOK(5), FIELDS_ARE_NOT_SET_CORRECTLY(4), CUSTOMER_NOT_FOUND(1),
        BOOK_NOT_FOUND(2), BOOK_NOT_ENOUGH_QUANTITY(3), USER_ALREADY_EXISTS(7), REQUEST_IS_NOT_AUTHORIZED(8), USER_IS_NOT_AUTHORIZED(9);
        private int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private ErrorCode errorCode;
    private List<String> messages = new ArrayList<>();

    public GeneralException(ErrorCode errorCode, String message) {
        super(message);
        messages.add(message);
        this.errorCode = errorCode;
    }

    public GeneralException(ErrorCode errorCode, List<String> messages) {
        super(messages.isEmpty() ? "" : messages.toString());
        this.messages.addAll(messages);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
