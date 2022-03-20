package com.getir.readingisgood.controller.advice;

import com.getir.readingisgood.model.ObjectResponse;
import com.getir.readingisgood.model.exception.GeneralException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
    A global exception handler class
 */
@ControllerAdvice
public class GeneralExceptionAdvicer {
    Logger logger = LoggerFactory.getLogger(GeneralExceptionAdvicer.class);

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ObjectResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        logger.error("", e);
        return new ResponseEntity<>(
                new ObjectResponse<>(new GeneralException(
                        GeneralException.ErrorCode.USER_IS_NOT_AUTHORIZED,
                        "User is not authorized to perform this request.")), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ObjectResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("", e);
        List<String> builder = new ArrayList<>();
        e.getConstraintViolations().forEach(err ->
                builder.add(err.getMessage())
        );
        return new ResponseEntity<>(
                new ObjectResponse<>(new GeneralException(
                        GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY, builder)), HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ObjectResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("", e);
        List<String> builder = new ArrayList<>();
        e.getAllErrors().forEach(err -> Arrays.asList(err.getArguments())
                .forEach(ea -> {
                    if (ea instanceof DefaultMessageSourceResolvable) {
                        DefaultMessageSourceResolvable ex = (DefaultMessageSourceResolvable) ea;
                        builder.add(ex.getCode() + " parameter is not set properly");
                    }
                }));
        return new ResponseEntity<>(
                new ObjectResponse<>(new GeneralException(
                        GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY, builder)), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ObjectResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("", e);
        return new ResponseEntity<>(
                new ObjectResponse<>(new GeneralException(
                        GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY, "Invalid request body")), HttpStatus.UNPROCESSABLE_ENTITY);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ObjectResponse<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("", e);
        return new ResponseEntity<>(
                new ObjectResponse<>(new GeneralException(
                        GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY,
                        e.getParameterName() + " parameter is not set properly")), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ObjectResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("", e);
        return new ResponseEntity<>(
                new ObjectResponse<>(new GeneralException(
                        GeneralException.ErrorCode.FIELDS_ARE_NOT_SET_CORRECTLY,
                        e.getName() + " parameter is not set properly")), HttpStatus.UNPROCESSABLE_ENTITY);
    }


}
