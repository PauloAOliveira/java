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
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

    private RowMapper<Brand> mapper = (resultSet, i) -> {
        Brand brand = new Brand();
        brand.setId(resultSet.getLong("id"));
        brand.setName(resultSet.getString("name"));
        brand.setDescription(resultSet.getString("description"));
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

        assertEquals("self", link.getRel());
        assertEquals("http://localhost:"+randomServerPort+"/brands/"+after.getId(), link.getHref());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getById() throws IOException {
        Brand before = findById(1L);

        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, 1L);
        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> after = objectMapper.readValue(response.getBody(), Map.class);

        assertEquals(before.getName(), after.get("name"));
        assertEquals(before.getDescription(), after.get("description"));

        Map<String, Object> links = (Map<String, Object>) after.get("_links");
        assertEquals(1, links.size());

        Map<String, String> link = (Map<String, String>) links.get("self");
        assertEquals(1, link.size());
        assertEquals("http://localhost:"+randomServerPort+"/brands/1", link.get("href"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getByIdNotFound() throws IOException {
        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, 999L);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Brand does not exist\"}]}", response.getBody());
    }
}
