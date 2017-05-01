package com.usecases.spring.domain;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class PersonRepresentationTest {

    private Faker faker;

    @Before
    public void setup() {
        faker = new Faker();
    }

    @Test
    public void of() throws Exception {
        String documentNumber = RandomStringUtils.randomNumeric(11);
        String first = faker.name().firstName();
        String last = faker.name().lastName();
        String email = faker.internet().emailAddress();
        LocalDateTime created = LocalDateTime.of(2000, 1, 2, 3, 4, 5);
        LocalDateTime lastUpdate = LocalDateTime.of(2001, 2, 3, 4, 5, 6);
        Boolean deleted = RandomUtils.nextInt(1, 11) % 2 == 0;

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