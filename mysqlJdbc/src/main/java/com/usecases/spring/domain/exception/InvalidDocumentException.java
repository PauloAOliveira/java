package com.usecases.spring.domain.exception;

import com.usecases.spring.exception.BadRequestException;

public class InvalidDocumentException extends BadRequestException{
    public InvalidDocumentException() {
        super("Document must be valid");
    }
}
