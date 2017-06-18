package com.usecases.spring.domain;

import com.usecases.spring.validator.constraint.Past;
import com.usecases.spring.validator.groups.Create;
import com.usecases.spring.validator.groups.Update;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Person implements Serializable{

    private static final long serialVersionUID = -3550498220222748157L;

    private Long id;

    @NotNull(groups = {Create.class})
    private DocumentType documentType;

    @Size(groups = {Create.class}, min = 11, max = 18)
    @NotNull(groups = {Create.class})
    private String documentNumber;

    @Size(groups = {Create.class, Update.class}, min = 4, max = 30)
    @NotNull(groups = {Create.class, Update.class})
    private String firstName;

    @Size(groups = {Create.class, Update.class}, min = 4, max = 45)
    @NotNull(groups = {Create.class, Update.class})
    private String lastName;

    @Email(groups = {Create.class, Update.class})
    @NotNull(groups = {Create.class, Update.class})
    private String email;

    @NotNull(groups = {Create.class, Update.class})
    @Past(groups = {Create.class, Update.class})
    private LocalDate birthDate;

    private LocalDateTime created;

    private LocalDateTime lastUpdate;

    private Boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
