package com.usecases.spring.validator.constraint;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class PastValidator implements ConstraintValidator<Past, LocalDate> {

    @Override
    public void initialize(Past constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

        if(value == null) {
            return true;
        }

        return value.isBefore(LocalDate.now());
    }
}
