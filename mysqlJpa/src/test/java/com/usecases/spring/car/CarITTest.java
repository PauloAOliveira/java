package com.usecases.spring.car;

import com.usecases.spring.IntegrationTest;
import com.usecases.spring.brand.BrandRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CarITTest extends IntegrationTest {

    private static final String ROOT_URL = "/brands/{brandId}/cars";
    private static final String ID_URL = ROOT_URL+"/{id}";

    @Autowired
    private CarRepository carRepository;

    @Before
    public void setup() {
        setMockMvc();
    }

    @Test
    public void createCar() throws Exception{
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

        String responseString = mockMvc.perform(post(ROOT_URL, 1L)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("rel", is("self")))
                .andExpect(jsonPath("href", notNullValue()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Object> response = parseResponse(responseString);

        String href = response.get("href").toString();
        String[] split = href.split("/");
        Long id = Long.valueOf(split[split.length-1]);

        Car after = carRepository.findOne(id).get();
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

        assertEquals(String.format("http://localhost/brands/1/cars/%d", after.getId()), href);
    }

    @Test
    public void createBrandNotFound() throws Exception {
        String json = "{" +
                "\"airbags\":true," +
                "\"name\":\"anyone\"," +
                "\"numberDoors\":3," +
                "\"color\":\"any one\"," +
                "\"manufactureYear\":1999," +
                "\"engine\":\"1.0\""+
                "}";
        mockMvc.perform(post(ROOT_URL, 999L)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0].error", is("Brand does not exist")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getById() throws Exception {
        Long id = 1L;
        Car before = carRepository.findOne(id).get();

        mockMvc.perform(get(ID_URL, 1L, id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name", is(before.getName())))
                .andExpect(jsonPath("numberDoors", is(before.getNumberDoors())))
                .andExpect(jsonPath("color", is(before.getColor())))
                .andExpect(jsonPath("manufactureYear", is(before.getManufactureYear())))
                .andExpect(jsonPath("airbags", is(before.getAirbags())))
                .andExpect(jsonPath("engine", is(before.getEngine().doubleValue())))
                .andExpect(jsonPath("version", is(before.getVersion().intValue())))
                .andExpect(jsonPath("links", hasSize(1)))
                .andExpect(jsonPath("links[0].rel", is("self")))
                .andExpect(jsonPath("links[0].href", is("http://localhost/brands/1/cars/1")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getByIdNotFound() throws Exception {
        mockMvc.perform(get(ID_URL, 1L, 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0].error", is("Car does not exist")));
    }

    @Test
    public void update() throws Exception{
        Car before = carRepository.findOne(2L).get();

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

        mockMvc.perform(put(ID_URL, 1L, 2L)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("rel", is("self")))
                .andExpect(jsonPath("href", is("http://localhost/brands/1/cars/2")));

        Car after = carRepository.findOne(2L).get();

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
    }

    @Test
    public void updateNotFound() throws Exception{
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

        mockMvc.perform(put(ID_URL, 1L, 999L)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0].error", is("Car does not exist")));
    }

    @Test
    public void updateDifferentVersion() throws Exception {
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

        mockMvc.perform(put(ID_URL, 1L, 3L)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0].error", is("Updating an old version.")));
    }
}
