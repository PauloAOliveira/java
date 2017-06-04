package com.usecases.spring.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.usecases.spring.Commons;
import com.usecases.spring.MysqlJdbcApplication;
import com.usecases.spring.domain.DocumentType;
import com.usecases.spring.domain.Person;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes = {MysqlJdbcApplication.class, Commons.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int randomServerPort;

    private Faker faker;

    private HttpHeaders httpHeaders;

    private RowMapper<Person> mapper = (resultSet, i) -> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Person preLoaded = new Person();
        preLoaded.setId(resultSet.getLong("id"));
        preLoaded.setDocumentType(DocumentType.valueOf(resultSet.getString("documentType")));
        preLoaded.setDocumentNumber(resultSet.getString("documentNumber"));
        preLoaded.setFirstName(resultSet.getString("firstName"));
        preLoaded.setLastName(resultSet.getString("lastName"));
        preLoaded.setEmail(resultSet.getString("email"));
        preLoaded.setBirthDate(LocalDate.parse(resultSet.getString("birthDate")));
        preLoaded.setCreated(LocalDateTime.parse(resultSet.getString("created").replaceAll("\\.[^.]*$", ""), formatter));
        preLoaded.setLastUpdate(LocalDateTime.parse(resultSet.getString("lastUpdate").replaceAll("\\.[^.]*$", ""), formatter));
        preLoaded.setDeleted(resultSet.getBoolean("deleted"));
        return preLoaded;
    };

    @Before
    public void setup() {
        faker = new Faker();
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
    }

    private Person findByDocument(String doc) {
        return jdbcTemplate.queryForObject("select * from Person where documentNumber = ?", mapper, doc);
    }

    private Person findById(Long id ) {
        return jdbcTemplate.queryForObject("select * from Person where id = ?", mapper, id);
    }

    @Test
    public void createPerson() {
        String documentNumber = "781.744.673-35";
        String firstName = faker.lorem().characters(4, 30);
        String lastName = faker.lorem().characters(4, 45);
        String email = faker.internet().emailAddress();
        String birthDate = LocalDate.now().minusDays(1).toString();
        String json = String.format(
                "{" +
                        "\"documentType\":\"CPF\"," +
                        "\"documentNumber\":\"%s\"," +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"birthDate\": \"%s\""+
                        "}", documentNumber, firstName, lastName, email, birthDate);

        ResponseEntity<Link> response = restTemplate.exchange("/people", HttpMethod.POST, new HttpEntity<>(json, httpHeaders), Link.class);
        assertEquals(201, response.getStatusCodeValue());

        Person after = findByDocument("78174467335");

        assertEquals("CPF", after.getDocumentType().name());
        assertEquals(firstName, after.getFirstName());
        assertEquals(lastName, after.getLastName());
        assertEquals(email, after.getEmail());
        assertEquals(birthDate, after.getBirthDate().toString());
        assertNotNull(after.getCreated());
        assertNotNull(after.getLastUpdate());

        Link link = response.getBody();
        assertEquals("self", link.getRel());
        assertEquals("http://localhost:"+randomServerPort+"/people/"+after.getId(), link.getHref());
    }

    @Test
    public void createPersonWithExistingDocument() {
        String documentNumber = "01403379386";
        String firstName = faker.lorem().characters(4, 30);
        String lastName = faker.lorem().characters(4, 45);
        String email = faker.internet().emailAddress();
        String birthDate = LocalDate.now().minusDays(1).toString();
        String json = String.format(
                "{" +
                        "\"documentType\":\"CPF\"," +
                        "\"documentNumber\":\"%s\"," +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"birthDate\": \"%s\""+
                        "}", documentNumber, firstName, lastName, email, birthDate);

        Person beforeTest = findByDocument("01403379386");

        ResponseEntity<String> response = restTemplate.exchange("/people", HttpMethod.POST, new HttpEntity<>(json, httpHeaders), String.class);
        assertEquals(409, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Person already exists.\"}]}", response.getBody());

        Person afterTest = findByDocument("01403379386");

        assertTrue(EqualsBuilder.reflectionEquals(beforeTest, afterTest));
    }

    @Test
    public void updatePersonPassingAllParameters() {
        Person before = findById(1L);

        String documentNumber = "8762jhfu827";
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String email = faker.internet().emailAddress();
        String birthDate = LocalDate.now().minusDays(1).toString();
        String json = String.format(
                "{" +
                        "\"documentType\":\"CNPJ\"," +
                        "\"documentNumber\":\"%s\"," +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"birthDate\": \"%s\""+
                        "}", documentNumber, firstName, lastName, email, birthDate);

        ResponseEntity<Link> response = restTemplate.exchange("/people/{personId}", HttpMethod.PUT,
                new HttpEntity<>(json, httpHeaders), Link.class, 1L);
        assertEquals(200, response.getStatusCodeValue());

        Person after = findById(1L);

        assertEquals(before.getDocumentType(), after.getDocumentType());
        assertEquals(before.getDocumentNumber(), after.getDocumentNumber());
        assertEquals(firstName, after.getFirstName());
        assertEquals(lastName, after.getLastName());
        assertEquals(email, after.getEmail());
        assertEquals(birthDate, after.getBirthDate().toString());
        assertNotNull(after.getCreated());
        assertNotNull(after.getLastUpdate());

        Link link = response.getBody();
        assertEquals("self", link.getRel());
        assertEquals("http://localhost:"+randomServerPort+"/people/"+after.getId(), link.getHref());
    }

    @Test
    public void updatePersonNotExist() {
        String documentNumber = "8762jhfu827";
        String firstName = faker.lorem().characters(4, 30);
        String lastName = faker.lorem().characters(4, 45);
        String email = faker.internet().emailAddress();
        String birthDate = LocalDate.now().minusDays(1).toString();
        String json = String.format(
                "{" +
                        "\"documentType\":\"CNPJ\"," +
                        "\"documentNumber\":\"%s\"," +
                        "\"firstName\":\"%s\"," +
                        "\"lastName\":\"%s\"," +
                        "\"email\":\"%s\"," +
                        "\"birthDate\": \"%s\""+
                        "}", documentNumber, firstName, lastName, email, birthDate);

        ResponseEntity<String> response = restTemplate.exchange("/people/{personId}", HttpMethod.PUT,
                new HttpEntity<>(json, httpHeaders), String.class, 1000L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Person does not exist\"}]}", response.getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getById() throws IOException {
        Person before = findById(6L);

        ResponseEntity<String> response = restTemplate.exchange("/people/{personId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, 6L);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> after = objectMapper.readValue(response.getBody(), Map.class);

        assertEquals(before.getDocumentType().toString(), after.get("documentType"));
        assertEquals(before.getDocumentNumber(), after.get("documentNumber"));
        assertEquals(before.getFirstName(), after.get("firstName"));
        assertEquals(before.getLastName(), after.get("lastName"));
        assertEquals(before.getEmail(), after.get("email"));
        assertEquals(before.getBirthDate().toString(), after.get("birthDate").toString());
        assertEquals(before.getCreated().toString(), after.get("created").toString());
        assertEquals(before.getLastUpdate().toString(), after.get("lastUpdate").toString());

        List<Map<String, Object>> links = (List<Map<String, Object>>) after.get("links");
        assertEquals(1, links.size());

        Map<String, Object> link = links.get(0);
        assertEquals(2, link.size());
        assertEquals("self", link.get("rel"));
        assertEquals("http://localhost:"+randomServerPort+"/people/6", link.get("href"));
    }

    @Test
    public void getByIdNotExist() throws IOException {
        ResponseEntity<String> response = restTemplate.exchange("/people/{personId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, 999L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Person does not exist\"}]}", response.getBody());
    }

    @Test
    public void getByIdDeleted() throws IOException {
        Person before = findById(4L);
        assertNotNull(before);

        ResponseEntity<String> response = restTemplate.exchange("/people/{personId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, 4L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Person does not exist\"}]}", response.getBody());
    }

    @Test
    public void delete() throws IOException {
        Person before = findById(7L);
        assertNotNull(before);

        ResponseEntity<String> response = restTemplate.exchange("/people/{personId}", HttpMethod.DELETE, new HttpEntity<>(httpHeaders), String.class, 7L);

        assertEquals(204, response.getStatusCodeValue());
        assertTrue(StringUtils.isBlank(response.getBody()));
    }

    @Test
    public void deleteNotExist() throws IOException {
        Person before = findById(4L);
        assertNotNull(before);

        ResponseEntity<String> response = restTemplate.exchange("/people/{personId}", HttpMethod.DELETE, new HttpEntity<>(httpHeaders), String.class, 4L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Person does not exist\"}]}", response.getBody());
    }
}
