package com.usecases.spring.domain.exception;

import com.usecases.spring.exception.NotFoundException;

public class PersonNotFoundException extends NotFoundException{

    public PersonNotFoundException() {
        super("Person does not exist");
    }
}
