package com.usecases.spring.exception;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BaseException extends RuntimeException implements Serializable {

    public BaseException(String message) {
        super(message);
    }
}
