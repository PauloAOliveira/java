package com.usecases.spring.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class BadRequestException extends BaseException {
    private static final long serialVersionUID = 2382707238147763716L;

    public BadRequestException(String message) {
        super(message);
    }
}
