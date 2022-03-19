package com.getir.readingisgood.model.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ExceptionObject {

    private List<String> messages;
    @Schema(description = "1: CUSTOMER NOT FOUND <br/>" +
            " 2: BOOK NOT FOUND <br/>" +
            " 3: BOOK NOT ENOUGH QUANTITY <br/>" +
            " 4: FIELDS_ARE_NOT_SET_CORRECTLY <br/>" +
            " 5: DUPLICATE_BOOK <br/>" +
            " 6: ORDER_NOT_FOUND <br/>" +
            " 7: USER_ALREADY_EXISTS <br/>" +
            " 8: REQUEST_IS_NOT_AUTHORIZED <br/>" +
            " 9: USER_IS_NOT_AUTHORIZED")
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
