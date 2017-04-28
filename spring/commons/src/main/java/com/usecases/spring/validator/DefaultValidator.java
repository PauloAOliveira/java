package com.usecases.spring.validator;

import java.util.Objects;

public class DefaultValidator {

    protected static void validateParameters(int size, Class<?>[] classes, Object... params) {
        Objects.requireNonNull(params, "Params must be present");
        Objects.requireNonNull(classes, "Classes must be present");
        if(params.length != size) {
            throw new IllegalArgumentException("Invalid number of parameters");
        }

        if(classes.length != params.length) {
            throw new IllegalArgumentException("Invalid number of classes");
        }


        int i = 0;
        for(Object p : params) {
            Objects.requireNonNull(p, "Parameter must be informed");
            if(!p.getClass().equals(classes[i])) {
                throw new IllegalArgumentException("Invalid parameters type");
            }
            i++;
        }
    }
}
