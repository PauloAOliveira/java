package com.usecases.spring.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@ResponseStatus(FORBIDDEN)
public class ForbiddenException extends BaseException {
    private static final long serialVersionUID = 5744967650450174286L;

    public ForbiddenException(String message) {
        super(message);
    }
}
