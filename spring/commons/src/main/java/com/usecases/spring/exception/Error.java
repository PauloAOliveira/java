package com.usecases.spring.exception;

import java.io.Serializable;

public class Error implements Serializable{
    private static final long serialVersionUID = -6083252831114560318L;

    private String error;

    public Error(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
