package com.usecases.spring.domain;

import org.springframework.hateoas.ResourceSupport;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PersonRepresentation extends ResourceSupport implements Serializable {

    private static final long serialVersionUID = -3564659492529880451L;

    private DocumentType documentType;

    private String documentNumber;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDate birthDate;

    private LocalDateTime created;

    private LocalDateTime lastUpdate;

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public static PersonRepresentation of(Person person) {
        PersonRepresentation p = new PersonRepresentation();
        p.birthDate = person.getBirthDate();
        p.documentNumber = person.getDocumentNumber();
        p.documentType = person.getDocumentType();
        p.firstName = person.getFirstName();
        p.lastName = person.getLastName();
        p.email = person.getEmail();
        p.created = person.getCreated();
        p.lastUpdate = person.getLastUpdate();
        return p;
    }
}
