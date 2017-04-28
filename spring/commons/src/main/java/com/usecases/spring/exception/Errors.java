package com.usecases.spring.exception;

import java.io.Serializable;
import java.util.List;

public class Errors implements Serializable {
    private static final long serialVersionUID = -2250921970272631162L;

    private List<Error> errors;

    public Errors(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
