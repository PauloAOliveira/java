package com.usecases.spring.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(CONFLICT)
public class ConflictException extends BaseException {

    private static final long serialVersionUID = -2239802121544583804L;

    public ConflictException(String message) {
        super(message);
    }
}
