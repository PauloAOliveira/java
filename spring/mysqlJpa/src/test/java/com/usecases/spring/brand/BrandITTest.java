package com.usecases.spring.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.usecases.spring.Commons;
import com.usecases.spring.MysqlJpaApplication;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Sql({"/data-test.sql"})
@SpringBootTest(classes = {MysqlJpaApplication.class, Commons.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BrandITTest {

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

    private DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RowMapper<Brand> mapper = (resultSet, i) -> {
        Brand brand = new Brand();
        brand.setId(resultSet.getLong("id"));
        brand.setName(resultSet.getString("name"));
        brand.setDescription(resultSet.getString("description"));

        String createdDate = resultSet.getString("created_date").replaceAll("\\.[^.]*$", "");
        String modifiedDate = resultSet.getString("modified_date").replaceAll("\\.[^.]*$", "");

        brand.setCreatedDate(LocalDateTime.parse(createdDate, pattern));
        brand.setModifiedDate(LocalDateTime.parse(modifiedDate, pattern));

        return brand;
    };

    @Before
    public void setup() {
        faker = new Faker();
        httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=UTF-8");
    }

    private Brand findById(Long id ) {
        return jdbcTemplate.queryForObject("select * from brand where id = ?", mapper, id);
    }

    @Test
    public void createBrand() {
        String name = faker.lorem().characters(4, 20);
        String description = faker.lorem().characters(1, 300);
        String json = String.format(
                "{" +
                        "\"name\":\"%s\"," +
                        "\"description\":\"%s\"" +
                        "}", name, description);

        ResponseEntity<Link> response = restTemplate.exchange("/brands", HttpMethod.POST, new HttpEntity<>(json, httpHeaders), Link.class);
        assertEquals(201, response.getStatusCodeValue());
        Link link = response.getBody();

        String[] split = link.getHref().split("/");
        Long id = Long.valueOf(split[split.length-1]);

        Brand after = findById(id);
        assertEquals(name, after.getName());
        assertEquals(description, after.getDescription());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getCreatedDate().truncatedTo(ChronoUnit.HOURS));
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getModifiedDate().truncatedTo(ChronoUnit.HOURS));

        assertEquals("self", link.getRel());
        assertEquals(String.format("http://localhost:%d/brands/%d",randomServerPort, after.getId()), link.getHref());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getById() throws IOException {
        Long id = 1L;
        Brand before = findById(id);

        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, id);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> after = objectMapper.readValue(response.getBody(), Map.class);

        assertEquals(before.getName(), after.get("name"));
        assertEquals(before.getDescription(), after.get("description"));

        List<Map<String, Object>> links = (List<Map<String, Object>>) after.get("links");
        assertEquals(1, links.size());

        Map<String, Object> link = links.get(0);
        assertEquals(2, link.size());
        assertEquals("self", link.get("rel"));
        assertEquals(String.format("http://localhost:%d/brands/%d",randomServerPort, id), link.get("href"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getByIdNotFound() throws IOException {
        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, 999L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Brand does not exist\"}]}", response.getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateNotFound() throws IOException {
        String body = "{\"name\":\"any name\"}";
        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}", HttpMethod.PUT, new HttpEntity<>(body, httpHeaders), String.class, 999L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Brand does not exist\"}]}", response.getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateNameAndDescription() throws IOException {
        Long id = 2L;
        Brand before = findById(id);

        String newName = "updated name";
        String newDescription = "updated description";

        String body = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", newName, newDescription);
        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}", HttpMethod.PUT, new HttpEntity<>(body, httpHeaders), String.class, id);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(String.format("{\"rel\":\"self\",\"href\":\"http://localhost:%d/brands/%d\"}",randomServerPort, id), response.getBody());

        Brand after = findById(id);

        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);

        assertNotEquals(before.getName(), responseBody.get("name"));
        assertNotEquals(before.getDescription(), responseBody.get("description"));
        assertEquals(after.getName(), newName);
        assertEquals(after.getDescription(), newDescription);
        assertEquals(LocalDateTime.of(2017,3,3,3,3,3), after.getCreatedDate());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getModifiedDate().truncatedTo(ChronoUnit.HOURS));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void updateDescriptionToNull() throws IOException {
        Long id = 3L;
        Brand before = findById(id);

        String newName = "updated name";

        String body = String.format("{\"name\":\"%s\"}", newName);
        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}", HttpMethod.PUT, new HttpEntity<>(body, httpHeaders), String.class, id);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(String.format("{\"rel\":\"self\",\"href\":\"http://localhost:%d/brands/%d\"}",randomServerPort, id), response.getBody());

        Brand after = findById(id);

        Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);

        assertNotEquals(before.getName(), responseBody.get("name"));
        assertNotNull(before.getDescription());

        assertEquals(after.getName(), newName);
        assertNull(after.getDescription());
    }
}
