package com.usecases.spring.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseStatus(NOT_FOUND)
public class NotFoundException extends BaseException {
    private static final long serialVersionUID = 5744967650450174286L;

    public NotFoundException(String message) {
        super(message);
    }
}
