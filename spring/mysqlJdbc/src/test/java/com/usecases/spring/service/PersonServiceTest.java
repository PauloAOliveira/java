package com.usecases.spring.service;

import com.github.javafaker.Faker;
import com.usecases.spring.domain.DocumentType;
import com.usecases.spring.domain.Person;
import com.usecases.spring.domain.PersonRepresentation;
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

    private Person personWithPunctuation;

    private Person personWithoutPunctuation;

    private LocalDateTime now;

    private Faker faker;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        now = LocalDateTime.now();
        faker = new Faker();

        personWithPunctuation = new Person();

        personWithPunctuation.setId(1l);
        personWithPunctuation.setDocumentType(DocumentType.CNPJ);
        personWithPunctuation.setDocumentNumber("97.875.528/0001-46");
        personWithPunctuation.setFirstName("First");
        personWithPunctuation.setLastName("Last");
        personWithPunctuation.setEmail("email@email.com");
        personWithPunctuation.setBirthDate(LocalDate.of(1990, 2, 3));
        personWithPunctuation.setCreated(now);
        personWithPunctuation.setLastUpdate(now);

        personWithoutPunctuation = new Person();

        personWithoutPunctuation.setId(2l);
        personWithoutPunctuation.setDocumentType(DocumentType.CPF);
        personWithoutPunctuation.setDocumentNumber("34842726350");
        personWithoutPunctuation.setFirstName("Manoel");
        personWithoutPunctuation.setLastName("Silva");
        personWithoutPunctuation.setEmail("mansil@mail.com");
        personWithoutPunctuation.setBirthDate(LocalDate.of(1970, 7, 8));
        personWithoutPunctuation.setCreated(now);
        personWithoutPunctuation.setLastUpdate(now);
    }

    @Test(expected = BadRequestException.class)
    public void saveWithInvalidDocument() {
        Person person = new Person();
        person.setDocumentType(DocumentType.CPF);
        person.setDocumentNumber("invalid one");

        try {
            personService.save(person);
        } catch (BadRequestException e) {
            assertEquals("Document must be valid", e.getMessage());
            throw e;
        }
    }

    @Test
    public void saveDocumentWithPunctuation() {
        Long idToTest = Long.valueOf(1852L);
        when(personRepository.create(eq(personWithPunctuation))).thenReturn(idToTest);

        Long id = personService.save(personWithPunctuation);

        assertEquals(idToTest, id);
        assertEquals("97875528000146", personWithPunctuation.getDocumentNumber());
        assertEquals("CNPJ", personWithPunctuation.getDocumentType().name());
        assertEquals("First", personWithPunctuation.getFirstName());
        assertEquals("Last", personWithPunctuation.getLastName());
        assertEquals("email@email.com", personWithPunctuation.getEmail());
        assertEquals(LocalDate.of(1990, 2, 3), personWithPunctuation.getBirthDate());
        assertEquals(now, personWithPunctuation.getCreated());
        assertEquals(now, personWithPunctuation.getLastUpdate());
    }

    @Test
    public void saveDocumentWithoutPunctuation() {
        Long idToTest = Long.valueOf(1852L);
        when(personRepository.create(eq(personWithoutPunctuation))).thenReturn(idToTest);

        Long id = personService.save(personWithoutPunctuation);

        assertEquals(idToTest, id);
        assertEquals("34842726350", personWithoutPunctuation.getDocumentNumber());
        assertEquals("CPF", personWithoutPunctuation.getDocumentType().name());
        assertEquals("Manoel", personWithoutPunctuation.getFirstName());
        assertEquals("Silva", personWithoutPunctuation.getLastName());
        assertEquals("mansil@mail.com", personWithoutPunctuation.getEmail());
        assertEquals(LocalDate.of(1970, 7, 8), personWithoutPunctuation.getBirthDate());
        assertEquals(now, personWithoutPunctuation.getCreated());
        assertEquals(now, personWithoutPunctuation.getLastUpdate());
    }

    @Test(expected = NotFoundException.class)
    public void updateNonexistentPerson() {
        Person person = new Person();

        when(personRepository.exist(eq(1234L))).thenReturn(false);

        try {
            personService.update(1234L, person);
        } catch (NotFoundException e) {
            assertEquals("Person does not exist", e.getMessage());
            verify(personRepository, times(0)).update(any(Long.class), any(Person.class));
            throw e;
        }
    }

    @Test
    public void updateExistentPerson() {
        Person person = new Person();

        when(personRepository.exist(eq(3L))).thenReturn(true);

        personService.update(3l, person);

        verify(personRepository, times(1)).update(eq(3l), eq(person));
    }

    @Test
    public void getById() {
        Person person = fakeCpf();

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

    @Test(expected = NotFoundException.class)
    public void getByIdNotFound() {
        Person person = fakeCpf();

        when(personRepository.getById(eq(person.getId()))).thenReturn(Optional.empty());

        try {
            personService.getById(person.getId());
        } catch (NotFoundException e) {
            assertEquals("Person does not exist", e.getMessage());
            throw e;
        }
    }

    @Test(expected = NotFoundException.class)
    public void deleteNonexistentPerson() {
        when(personRepository.exist(eq(1234L))).thenReturn(false);

        try {
            personService.delete(1234L);
        } catch (NotFoundException e) {
            assertEquals("Person does not exist", e.getMessage());
            verify(personRepository, times(0)).update(any(Long.class), any(Person.class));
            throw e;
        }
    }

    @Test
    public void deleteExistentPerson() {
        when(personRepository.exist(eq(5L))).thenReturn(true);

        personService.delete(5l);

        verify(personRepository, times(1)).delete(eq(5l));
    }

    private Person fakeCpf() {
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
        return person;
    }
}