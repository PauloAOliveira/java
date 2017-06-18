package com.usecases.spring.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

@ResponseStatus(CONFLICT)
public class VersionConflictException extends BaseException {

    private static final long serialVersionUID = -2155435825968280744L;

    public VersionConflictException() {
        super("Updating an old version.");
    }
}
