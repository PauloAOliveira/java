package com.usecases.spring.validator;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DocumentValidatorTest {

    private static final String CPF_LOWER = "cpf";
    private static final String CPF_UPPER = "CPF";
    private static final String CNPJ_LOWER = "cnpj";
    private static final String CNPJ_UPPER = "CNPJ";
    private static final String PASSPORT_LOWER = "passport";
    private static final String PASSPORT_UPPER = "PASSPORT";

    @Test
    public void invalidLowerCpfWithPunctuation() {
        assertFalse(DocumentValidator.isValid(CPF_LOWER, "547.367.675-81"));
    }

    @Test
    public void invalidLowerCpfWithMoreNumbers() {
        assertFalse(DocumentValidator.isValid(CPF_LOWER, "645.477.254-381"));
    }

    @Test
    public void invalidLowerCpfWithLessNumbers() {
        assertFalse(DocumentValidator.isValid(CPF_LOWER, "45.767.820-88"));
    }

    @Test
    public void invalidLowerCpfWithoutPunctuation() {
        assertFalse(DocumentValidator.isValid(CPF_LOWER, "54736767581"));
    }

    @Test
    public void validLowerCpfWithPunctuation() {
        assertTrue(DocumentValidator.isValid(CPF_LOWER, "645.477.254-38"));
    }

    @Test
    public void validLowerCpfWithoutPunctuation() {
        assertTrue(DocumentValidator.isValid(CPF_LOWER, "64547725438"));
    }

    @Test
    public void invalidUpperCpfWithPunctuation() {
        assertFalse(DocumentValidator.isValid(CPF_UPPER, "547.367.675-81"));
    }

    @Test
    public void invalidUpperCpfWithoutPunctuation() {
        assertFalse(DocumentValidator.isValid(CPF_UPPER, "54736767581"));
    }

    @Test
    public void validUpperCpfWithPunctuation() {
        assertTrue(DocumentValidator.isValid(CPF_UPPER, "645.477.254-38"));
    }

    @Test
    public void validUpperCpfWithoutPunctuation() {
        assertTrue(DocumentValidator.isValid(CPF_UPPER, "64547725438"));
    }


    @Test
    public void invalidLowerCnpjWithPunctuation() {
        assertFalse(DocumentValidator.isValid(CNPJ_LOWER, "04.178.373/0001-09"));
    }

    @Test
    public void invalidLowerCnpjWithMoreNumbers() {
        assertFalse(DocumentValidator.isValid(CNPJ_LOWER, "35.185.084/0001-601"));
    }

    @Test
    public void invalidLowerCnpjWithLessNumbers() {
        assertFalse(DocumentValidator.isValid(CNPJ_LOWER, "2.073.426/0001-09"));
    }

    @Test
    public void invalidLowerCnpjWithoutPunctuation() {
        assertFalse(DocumentValidator.isValid(CNPJ_LOWER, "04178373000109"));
    }

    @Test
    public void validLowerCnpjWithPunctuation() {
        assertTrue(DocumentValidator.isValid(CNPJ_LOWER, "35.185.084/0001-60"));
    }

    @Test
    public void validLowerCnpjWithoutPunctuation() {
        assertTrue(DocumentValidator.isValid(CNPJ_LOWER, "02073426000109"));
    }

    @Test
    public void invalidUpperCnpjWithPunctuation() {
        assertFalse(DocumentValidator.isValid(CNPJ_UPPER, "04.178.373/0001-09"));
    }

    @Test
    public void invalidUpperCnpjWithMoreNumbers() {
        assertFalse(DocumentValidator.isValid(CNPJ_UPPER, "35.185.084/0001-601"));
    }

    @Test
    public void invalidUpperCnpjWithLessNumbers() {
        assertFalse(DocumentValidator.isValid(CNPJ_UPPER, "2.073.426/0001-09"));
    }

    @Test
    public void invalidUpperCnpjWithoutPunctuation() {
        assertFalse(DocumentValidator.isValid(CNPJ_UPPER, "04178373000109"));
    }

    @Test
    public void validUpperCnpjWithPunctuation() {
        assertTrue(DocumentValidator.isValid(CNPJ_UPPER, "35.185.084/0001-60"));
    }

    @Test
    public void validUpperCnpjWithoutPunctuation() {
        assertTrue(DocumentValidator.isValid(CNPJ_UPPER, "02073426000109"));
    }

    @Test
    public void validLowerPassportWithPunctuation() {
        assertTrue(DocumentValidator.isValid(PASSPORT_LOWER, "uus-88.137/sg"));
    }

    @Test
    public void validLowerPassportWithoutPunctuation() {
        assertTrue(DocumentValidator.isValid(PASSPORT_LOWER, "uus882137sg"));
    }

    @Test
    public void validUpperPassportWithPunctuation() {
        assertTrue(DocumentValidator.isValid(PASSPORT_UPPER, "uus-88.137/sg"));
    }

    @Test
    public void validUpperPassportWithoutPunctuation() {
        assertTrue(DocumentValidator.isValid(PASSPORT_UPPER, "uus882137sg"));
    }
}
