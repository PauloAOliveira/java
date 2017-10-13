package com.usecases.spring.repository;

import com.github.javafaker.Faker;
import com.usecases.spring.MysqlJdbcApplication;
import com.usecases.spring.domain.DocumentType;
import com.usecases.spring.domain.Person;
import com.usecases.spring.exception.ConflictException;
import com.usecases.spring.repository.PersonRepository;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = MysqlJdbcApplication.class)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Faker faker;
    private Person preLoaded;
    private Person preLoadedToUpdateDocument;

    private Person queryToTest(Long id) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from Person where id = ?", id);
        sqlRowSet.first();

        Person preLoaded = new Person();
        preLoaded.setId(id);
        preLoaded.setDocumentType(DocumentType.valueOf(sqlRowSet.getString("documentType")));
        preLoaded.setDocumentNumber(sqlRowSet.getString("documentNumber"));
        preLoaded.setFirstName(sqlRowSet.getString("firstName"));
        preLoaded.setLastName(sqlRowSet.getString("lastName"));
        preLoaded.setEmail(sqlRowSet.getString("email"));
        preLoaded.setBirthDate(LocalDate.parse(sqlRowSet.getString("birthDate")));
        preLoaded.setCreated(LocalDateTime.parse(sqlRowSet.getString("created").replaceAll("\\.[^.]*$", ""), formatter));
        preLoaded.setLastUpdate(LocalDateTime.parse(sqlRowSet.getString("lastUpdate").replaceAll("\\.[^.]*$", ""), formatter));
        preLoaded.setDeleted(sqlRowSet.getBoolean("deleted"));
        return preLoaded;
    }

    @Before
    public void setup() {
        faker = new Faker();
        preLoaded = queryToTest(1l);
        preLoadedToUpdateDocument = queryToTest(3l);
    }

    @Test
    public void createWithAllParametersSucess() {
        Person person = new Person();
        person.setDocumentNumber("123456789");
        person.setDocumentType(DocumentType.CPF);
        person.setFirstName("name1");
        person.setLastName("last1");
        person.setEmail("test@test.com");
        person.setBirthDate(LocalDate.of(1990, 1,1));
        person.setDeleted(false);

        String now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();
        Long id = personRepository.create(person);

        assertNotNull(id);
        Person created = queryToTest(id);

        assertTrue(EqualsBuilder.reflectionEquals(person, created, "id", "created", "lastUpdate"));
        assertEquals(now, created.getCreated().toString().substring(0, 19));
        assertEquals(now, created.getLastUpdate().toString().substring(0, 19));
    }

    @Test(expected = ConflictException.class)
    public void createWithDocumentAlreadyCreated() {
        Person person = new Person();
        person.setDocumentNumber("01403379386");
        person.setDocumentType(DocumentType.CPF);
        person.setFirstName("name1");
        person.setLastName("last1");
        person.setEmail("test@test.com");
        person.setBirthDate(LocalDate.of(1990, 1,1));

        personRepository.create(person);
    }

    @Test
    public void getByIdExistingPerson() {
        Optional<Person> personOp = personRepository.getById(1l);

        assertTrue(personOp.isPresent());
        Person toTest = personOp.get();
        assertTrue(EqualsBuilder.reflectionEquals(preLoaded, toTest));
    }

    @Test
    public void getByIdNotExisting() {
        Optional<Person> personOp = personRepository.getById(999l);

        assertFalse(personOp.isPresent());
    }

    @Test
    public void getByIdExistingButDeleted() {
        List<Map<String, Object>> objects = jdbcTemplate.queryForList("SELECT * FROM Person WHERE id = 4");
        assertTrue(objects.size() == 1);

        Optional<Person> personOp = personRepository.getById(4l);

        assertFalse(personOp.isPresent());
    }

    @Test
    public void exist() {
        assertTrue(personRepository.exist(1l));
    }

    @Test
    public void notExist() {
        assertFalse(personRepository.exist(1000l));
    }

    @Test
    public void updatePersonByIdDifferentDocumentNumber() {
        Person beforeUpdate = queryToTest(3l);
        assertTrue(EqualsBuilder.reflectionEquals(preLoadedToUpdateDocument, beforeUpdate));

        Person toUpdate = new Person();
        toUpdate.setId(4l);
        toUpdate.setDocumentType(DocumentType.CPF);
        toUpdate.setDocumentNumber("28015243106");
        toUpdate.setFirstName("Alterado2");
        toUpdate.setLastName("After2 Update2");
        toUpdate.setEmail("after2@update2.com");
        toUpdate.setBirthDate(LocalDate.now());
        toUpdate.setCreated(LocalDateTime.now());
        toUpdate.setLastUpdate(LocalDateTime.of(1992,11,12,13,14,15));
        toUpdate.setDeleted(false);

        Long id = personRepository.update(3l, toUpdate);

        assertEquals(3l, id.longValue());
        Person afterUpdate = queryToTest(3l);

        assertTrue(EqualsBuilder.reflectionEquals(toUpdate, afterUpdate, "id", "documentType","documentNumber","created", "lastUpdate"));
        assertEquals(DocumentType.PASSPORT, afterUpdate.getDocumentType());
        assertEquals("ADGJ15985AD", afterUpdate.getDocumentNumber());
        assertEquals(beforeUpdate.getCreated(), afterUpdate.getCreated());
        assertNotEquals(beforeUpdate.getLastUpdate(), afterUpdate.getLastUpdate());
    }

    @Test
    public void delete() {
        Person before = queryToTest(5l);
        assertFalse(before.getDeleted());

        personRepository.delete(5l);

        Person after = queryToTest(5l);
        assertTrue(after.getDeleted());
    }
}
