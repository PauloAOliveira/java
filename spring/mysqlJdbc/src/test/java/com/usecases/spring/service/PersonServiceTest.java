package com.usecases.spring.service;

import com.github.javafaker.Faker;
import com.usecases.spring.domain.DocumentType;
import com.usecases.spring.domain.Person;
import com.usecases.spring.domain.PersonRepresentation;
import com.usecases.spring.domain.exception.InvalidDocumentException;
import com.usecases.spring.domain.exception.PersonNotFoundException;
import com.usecases.spring.exception.BadRequestException;
import com.usecases.spring.exception.NotFoundException;
import com.usecases.spring.repository.PersonRepository;
import com.usecases.spring.service.PersonService;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    private LocalDateTime now;

    private Faker faker;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        now = LocalDateTime.now();
        faker = new Faker();
    }

    @Test(expected = InvalidDocumentException.class)
    public void saveWithInvalidDocument() {
        Person person = new Person();
        person.setDocumentType(DocumentType.CPF);
        person.setDocumentNumber("invalid one");

        personService.save(person);
    }

    @Test
    public void saveDocumentWithPunctuation() {
        String first = faker.lorem().characters(4, 20);
        String last = faker.lorem().characters(4, 30);
        String email = faker.internet().emailAddress();
        LocalDate birthDate = LocalDate.of(1990, 2, 3);
        DocumentType documentType = DocumentType.CNPJ;

        Person withPunctuation = new Person();
        withPunctuation.setDocumentType(documentType);
        withPunctuation.setDocumentNumber("97.875.528/0001-46");
        withPunctuation.setFirstName(first);
        withPunctuation.setLastName(last);
        withPunctuation.setEmail(email);
        withPunctuation.setBirthDate(birthDate);

        Long idToTest = 1852L;
        when(personRepository.create(eq(withPunctuation))).thenReturn(idToTest);

        Long id = personService.save(withPunctuation);

        //Validating if object's values were not modified
        assertEquals(idToTest, id);
        assertEquals("97875528000146", withPunctuation.getDocumentNumber());
        assertEquals(documentType.name(), withPunctuation.getDocumentType().name());
        assertEquals(first, withPunctuation.getFirstName());
        assertEquals(last, withPunctuation.getLastName());
        assertEquals(email, withPunctuation.getEmail());
        assertEquals(birthDate, withPunctuation.getBirthDate());
    }

    @Test
    public void saveDocumentWithoutPunctuation() {
        String first = faker.lorem().characters(4, 20);
        String last = faker.lorem().characters(4, 30);
        String email = faker.internet().emailAddress();
        LocalDate birthDate = LocalDate.of(1990, 2, 3);
        DocumentType documentType = DocumentType.CPF;

        Person withoutPunctuation = new Person();

        withoutPunctuation.setDocumentType(documentType);
        withoutPunctuation.setDocumentNumber("34842726350");
        withoutPunctuation.setFirstName(first);
        withoutPunctuation.setLastName(last);
        withoutPunctuation.setEmail(email);
        withoutPunctuation.setBirthDate(birthDate);

        Long idToTest = 1852L;
        when(personRepository.create(eq(withoutPunctuation))).thenReturn(idToTest);

        Long id = personService.save(withoutPunctuation);

        assertEquals(idToTest, id);
        assertEquals("34842726350", withoutPunctuation.getDocumentNumber());
        assertEquals(documentType.name(), withoutPunctuation.getDocumentType().name());
        assertEquals(first, withoutPunctuation.getFirstName());
        assertEquals(last, withoutPunctuation.getLastName());
        assertEquals(email, withoutPunctuation.getEmail());
        assertEquals(birthDate, withoutPunctuation.getBirthDate());
    }

    @Test(expected = PersonNotFoundException.class)
    public void updateNonexistentPerson() {
        Person person = new Person();

        when(personRepository.exist(eq(1234L))).thenReturn(false);

        try {
            personService.update(1234L, person);
        } catch (PersonNotFoundException e) {
            verify(personRepository, times(0)).update(any(Long.class), any(Person.class));
            throw e;
        }
    }

    @Test
    public void updateExistentPerson() {
        Person person = new Person();

        when(personRepository.exist(eq(3L))).thenReturn(true);

        personService.update(3L, person);

        verify(personRepository, times(1)).update(eq(3L), eq(person));
    }

    @Test
    public void getById() {
        Person person = new Person();
        person.setId(faker.number().randomNumber());
        person.setDocumentType(DocumentType.CPF);
        person.setDocumentNumber(faker.number().digits(11));
        person.setFirstName(faker.name().firstName());
        person.setLastName(faker.name().lastName());
        person.setEmail(faker.internet().emailAddress());
        person.setBirthDate(LocalDate.MIN);
        person.setCreated(LocalDateTime.now());
        person.setLastUpdate(LocalDateTime.now());

        when(personRepository.getById(eq(person.getId()))).thenReturn(Optional.of(person));

        PersonRepresentation resp = personService.getById(person.getId());

        assertEquals(person.getDocumentType(), resp.getDocumentType());
        assertEquals(person.getDocumentNumber(), resp.getDocumentNumber());
        assertEquals(person.getFirstName(), resp.getFirstName());
        assertEquals(person.getLastName(), resp.getLastName());
        assertEquals(person.getEmail(), resp.getEmail());
        assertEquals(person.getBirthDate(), resp.getBirthDate());
        assertEquals(person.getCreated(), resp.getCreated());
        assertEquals(person.getLastUpdate(), resp.getLastUpdate());
    }

    @Test(expected = PersonNotFoundException.class)
    public void getByIdNotFound() {
        Long id = faker.number().randomNumber();

        when(personRepository.getById(eq(id))).thenReturn(Optional.empty());

        personService.getById(id);
    }

    @Test(expected = PersonNotFoundException.class)
    public void deleteNonexistentPerson() {
        when(personRepository.exist(eq(1234L))).thenReturn(false);

        try {
            personService.delete(1234L);
        } catch (PersonNotFoundException e) {
            verify(personRepository, times(0)).update(any(Long.class), any(Person.class));
            throw e;
        }
    }

    @Test
    public void deleteExistentPerson() {
        when(personRepository.exist(eq(5L))).thenReturn(true);

        personService.delete(5L);

        verify(personRepository, times(1)).delete(eq(5L));
    }
}