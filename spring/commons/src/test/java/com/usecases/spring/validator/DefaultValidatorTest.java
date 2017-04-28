package com.usecases.spring.validator;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class DefaultValidatorTest {

    @Test(expected = NullPointerException.class)
    public void validateParametersNullParams() {
        try {
            DefaultValidator.validateParameters(0, null, null);
        } catch (NullPointerException e) {
            assertEquals("Params must be present", e.getMessage());
            throw e;
        }
    }

    @Test(expected = NullPointerException.class)
    public void validateParametersNullClasses() {
        try {
            DefaultValidator.validateParameters(0, null, new Object[]{""});
        } catch (NullPointerException e) {
            assertEquals("Classes must be present", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateParametersDifferentSize() {
        try {
            DefaultValidator.validateParameters(2, new Class[]{String.class}, new Object[]{""});
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid number of parameters", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateParametersDifferentSizeClassAndParams() {
        try {
            DefaultValidator.validateParameters(2, new Class[]{String.class}, new Object[]{"", ""});
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid number of classes", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateParametersDifferentClassesFirstArgument() {
        try {
            DefaultValidator.validateParameters(2, new Class[]{Integer.class, String.class}, new Object[]{"", ""});
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid parameters type", e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateParametersDifferentClassesSecondArgument() {
        try {
            DefaultValidator.validateParameters(2, new Class[]{String.class, Integer.class}, new Object[]{"", ""});
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid parameters type", e.getMessage());
            throw e;
        }
    }

    @Test(expected = NullPointerException.class)
    public void validateParametersNullValueFirstArgument() {
        String aux = null;
        try {
            DefaultValidator.validateParameters(2, new Class[]{String.class, String.class}, new Object[]{aux, ""});
        } catch (NullPointerException e) {
            assertEquals("Parameter must be informed", e.getMessage());
            throw e;
        }
    }

    @Test(expected = NullPointerException.class)
    public void validateParametersNullValueSecondArgument() {
        String aux = null;
        try {
            DefaultValidator.validateParameters(2, new Class[]{String.class, String.class}, new Object[]{"", aux});
        } catch (NullPointerException e) {
            assertEquals("Parameter must be informed", e.getMessage());
            throw e;
        }
    }

    protected static void validateParameters(int size, Class<?>[] classes, Object... params) {
        int i = 0;
        for(Object p : params) {
            if(!p.getClass().equals(classes[i].getClass())) {
                throw new IllegalArgumentException("Invalid parameters type");
            }
            Objects.requireNonNull(p, "Parameter must be informed");
            i++;
        }
    }
}
