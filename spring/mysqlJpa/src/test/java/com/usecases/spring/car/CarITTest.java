package com.usecases.spring.car;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.usecases.spring.Commons;
import com.usecases.spring.MysqlJpaApplication;
import com.usecases.spring.brand.Brand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.hateoas.Link;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.Column;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@Sql({"/data-test.sql"})
@SpringBootTest(classes = {MysqlJpaApplication.class, Commons.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarITTest {

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

    private RowMapper<Car> carMapper = (resultSet, i) -> {
        Car car = new Car();
        car.setId(resultSet.getLong("id"));
        car.setName(resultSet.getString("name"));
        car.setNumberDoors(resultSet.getInt("number_doors"));
        car.setColor(resultSet.getString("color"));
        car.setManufactureYear(resultSet.getInt("manufacture_year"));
        car.setAirbags(resultSet.getBoolean("airbags"));
        car.setEngine(resultSet.getBigDecimal("engine"));
        car.setVersion(resultSet.getLong("version"));

        String createdDate = resultSet.getString("created_date").replaceAll("\\.[^.]*$", "");
        String modifiedDate = resultSet.getString("modified_date").replaceAll("\\.[^.]*$", "");

        car.setCreatedDate(LocalDateTime.parse(createdDate, pattern));
        car.setModifiedDate(LocalDateTime.parse(modifiedDate, pattern));

        Brand brand = new Brand();
        brand.setId(resultSet.getLong("brand_id"));
        car.setBrand(brand);
        return car;
    };

    private RowMapper<Brand> brandMapper = (resultSet, i) -> {
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

    private Car findById(Long id) {
        Car car = jdbcTemplate.queryForObject("select * from car where id = ?", carMapper, id);
        car.setBrand(jdbcTemplate.queryForObject("select * from brand where id = ?", brandMapper, car.getBrand().getId()));
        return car;
    }

    @Test
    public void createCar() {
        String name = faker.lorem().characters(5, 10);
        Integer numberDoors = faker.number().numberBetween(2, 5);
        String color = faker.color().name();
        Integer manufactureYear = faker.number().numberBetween(1990, 2020);
        Boolean airbags = faker.bool().bool();
        BigDecimal engine = new BigDecimal(faker.number().randomDouble(1,1,5))
                .setScale(1, BigDecimal.ROUND_UP);

        String json = String.format("{" +
                "\"airbags\":%s," +
                "\"name\":\"%s\"," +
                "\"numberDoors\":%d," +
                "\"color\":\"%s\"," +
                "\"manufactureYear\":%d," +
                "\"engine\":\"%s\""+
                "}", airbags, name, numberDoors, color, manufactureYear, engine);

        ResponseEntity<Link> response = restTemplate.exchange("/brands/{brandId}/cars", HttpMethod.POST, new HttpEntity<>(json, httpHeaders), Link.class, 1L);
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        Link link = response.getBody();

        String[] split = link.getHref().split("/");
        Long id = Long.valueOf(split[split.length-1]);

        Car after = findById(id);
        assertEquals(name, after.getName());
        assertEquals(Long.valueOf(1L), after.getBrand().getId());
        assertEquals(numberDoors, after.getNumberDoors());
        assertEquals(color, after.getColor());
        assertEquals(manufactureYear, after.getManufactureYear());
        assertEquals(airbags, after.getAirbags());
        assertEquals(engine, after.getEngine());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getCreatedDate().truncatedTo(ChronoUnit.HOURS));
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getModifiedDate().truncatedTo(ChronoUnit.HOURS));
        assertEquals(Long.valueOf(0), after.getVersion());

        assertEquals("self", link.getRel());
        assertEquals(String.format("http://localhost:%d/cars/%d",randomServerPort, after.getId()), link.getHref());
    }

    @Test
    public void createBrandNotFound() throws IOException {
        String json = "{" +
                "\"airbags\":true," +
                "\"name\":\"anyone\"," +
                "\"numberDoors\":3," +
                "\"color\":\"any one\"," +
                "\"manufactureYear\":1999," +
                "\"engine\":\"1.0\""+
                "}";
        ResponseEntity<String> response = restTemplate.exchange("/brands/{brandId}/cars", HttpMethod.POST, new HttpEntity<>(json, httpHeaders), String.class, 999L);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Brand does not exist\"}]}", response.getBody());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getById() throws IOException {
        Long id = 1L;
        Car before = findById(id);

        ResponseEntity<String> response = restTemplate.exchange("/cars/{carId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, id);
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        Map<String, Object> after = objectMapper.readValue(response.getBody(), Map.class);

        assertEquals(before.getName(), after.get("name"));
        assertEquals(before.getNumberDoors(), after.get("numberDoors"));
        assertEquals(before.getColor(), after.get("color"));
        assertEquals(before.getManufactureYear(), after.get("manufactureYear"));
        assertEquals(before.getAirbags(), after.get("airbags"));
        assertEquals(before.getEngine().doubleValue(), after.get("engine"));
        assertEquals(before.getVersion().intValue(), after.get("version"));

        Map<String, Object> brand = (Map<String, Object>) after.get("brand");
        assertEquals("self", brand.get("rel"));
        assertEquals(String.format("http://localhost:%d/brands/%d",randomServerPort, before.getBrand().getId()), brand.get("href"));

        List<Map<String, Object>> links = (List<Map<String, Object>>) after.get("links");
        assertEquals(1, links.size());

        Map<String, Object> link = links.get(0);
        assertEquals(2, link.size());
        assertEquals("self", link.get("rel"));
        assertEquals(String.format("http://localhost:%d/cars/%d",randomServerPort, id), link.get("href"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getByIdNotFound() throws IOException {
        ResponseEntity<String> response = restTemplate.exchange("/cars/{carId}", HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class, 999L);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Car does not exist\"}]}", response.getBody());
    }

    @Test
    public void update() {
        Car before = findById(2L);

        String name = faker.lorem().characters(5, 10);
        Integer numberDoors = faker.number().numberBetween(2, 5);
        String color = faker.color().name();
        Integer manufactureYear = faker.number().numberBetween(1990, 2020);
        Boolean airbags = faker.bool().bool();
        BigDecimal engine = new BigDecimal(faker.number().randomDouble(1,1,5))
                .setScale(1, BigDecimal.ROUND_UP);

        String json = String.format("{" +
                "\"airbags\":%s," +
                "\"name\":\"%s\"," +
                "\"numberDoors\":%d," +
                "\"color\":\"%s\"," +
                "\"manufactureYear\":%d," +
                "\"engine\":\"%s\"," +
                "\"version\":2"+
                "}", airbags, name, numberDoors, color, manufactureYear, engine);

        ResponseEntity<Link> response = restTemplate.exchange("/cars/{id}", HttpMethod.PUT, new HttpEntity<>(json, httpHeaders), Link.class, 2L);
        assertEquals(HttpStatus.ACCEPTED.value(), response.getStatusCodeValue());
        Link link = response.getBody();

        String[] split = link.getHref().split("/");
        Long id = Long.valueOf(split[split.length-1]);

        Car after = findById(id);
        assertEquals(name, after.getName());
        assertEquals(before.getBrand().getId(), after.getBrand().getId());
        assertEquals(numberDoors, after.getNumberDoors());
        assertEquals(color, after.getColor());
        assertEquals(manufactureYear, after.getManufactureYear());
        assertEquals(airbags, after.getAirbags());
        assertEquals(engine, after.getEngine());
        assertEquals(before.getCreatedDate(), after.getCreatedDate());
        assertEquals(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS), after.getModifiedDate().truncatedTo(ChronoUnit.HOURS));
        assertEquals(before.getVersion()+1, after.getVersion().intValue());

        assertEquals("self", link.getRel());
        assertEquals(String.format("http://localhost:%d/cars/%d",randomServerPort, before.getId()), link.getHref());
    }

    @Test
    public void updateNotFound() {
        String name = faker.lorem().characters(5, 10);
        Integer numberDoors = faker.number().numberBetween(2, 5);
        String color = faker.color().name();
        Integer manufactureYear = faker.number().numberBetween(1990, 2020);
        Boolean airbags = faker.bool().bool();
        BigDecimal engine = new BigDecimal(faker.number().randomDouble(1,1,5))
                .setScale(1, BigDecimal.ROUND_UP);

        String json = String.format("{" +
                "\"airbags\":%s," +
                "\"name\":\"%s\"," +
                "\"numberDoors\":%d," +
                "\"color\":\"%s\"," +
                "\"manufactureYear\":%d," +
                "\"engine\":\"%s\"," +
                "\"version\":2"+
                "}", airbags, name, numberDoors, color, manufactureYear, engine);

        ResponseEntity<String> response = restTemplate.exchange("/cars/{id}", HttpMethod.PUT, new HttpEntity<>(json, httpHeaders), String.class, 999L);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Car does not exist\"}]}", response.getBody());
    }

    @Test
    public void updateDifferentVersion() {
        String name = faker.lorem().characters(5, 10);
        Integer numberDoors = faker.number().numberBetween(2, 5);
        String color = faker.color().name();
        Integer manufactureYear = faker.number().numberBetween(1990, 2020);
        Boolean airbags = faker.bool().bool();
        BigDecimal engine = new BigDecimal(faker.number().randomDouble(1,1,5))
                .setScale(1, BigDecimal.ROUND_UP);

        String json = String.format("{" +
                "\"airbags\":%s," +
                "\"name\":\"%s\"," +
                "\"numberDoors\":%d," +
                "\"color\":\"%s\"," +
                "\"manufactureYear\":%d," +
                "\"engine\":\"%s\"," +
                "\"version\":4"+
                "}", airbags, name, numberDoors, color, manufactureYear, engine);

        ResponseEntity<String> response = restTemplate.exchange("/cars/{id}", HttpMethod.PUT, new HttpEntity<>(json, httpHeaders), String.class, 3L);
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatusCodeValue());
        assertEquals("{\"errors\":[{\"error\":\"Updating an old version.\"}]}", response.getBody());
    }
}
