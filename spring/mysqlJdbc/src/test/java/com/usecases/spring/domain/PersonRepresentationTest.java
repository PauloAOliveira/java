package com.usecases.spring.domain;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class PersonRepresentationTest {

    @Test
    public void of() throws Exception {
        String documentNumber = "25896547412";
        String first = "First";
        String last = "Last";
        String email = "email@email.com";
        LocalDateTime created = LocalDateTime.of(2000, 1, 2, 3, 4, 5);
        LocalDateTime lastUpdate = LocalDateTime.of(2001, 2, 3, 4, 5, 6);
        Boolean deleted = Boolean.TRUE;

        Person person = new Person();
        person.setId(123L);
        person.setDocumentType(DocumentType.CNPJ);
        person.setDocumentNumber(documentNumber);
        person.setFirstName(first);
        person.setLastName(last);
        person.setEmail(email);
        LocalDate birthDate = LocalDate.of(2000, 1, 2);
        person.setBirthDate(birthDate);
        person.setCreated(created);
        person.setLastUpdate(lastUpdate);
        person.setDeleted(deleted);

        PersonRepresentation rep = PersonRepresentation.of(person);

        assertEquals(DocumentType.CNPJ, rep.getDocumentType());
        assertEquals(documentNumber, rep.getDocumentNumber());
        assertEquals(first, rep.getFirstName());
        assertEquals(last, rep.getLastName());
        assertEquals(email, rep.getEmail());
        assertEquals(birthDate, rep.getBirthDate());
        assertEquals(created, rep.getCreated());
        assertEquals(lastUpdate, rep.getLastUpdate());
    }

}